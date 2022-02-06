/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.application;

import bisq.account.AccountService;
import bisq.account.accounts.RevolutAccount;
import bisq.account.accounts.SepaAccount;
import bisq.common.locale.CountryRepository;
import bisq.common.locale.LocaleRepository;
import bisq.common.threading.ExecutorFactory;
import bisq.common.util.CompletableFutureUtils;
import bisq.i18n.Res;
import bisq.identity.IdentityService;
import bisq.network.NetworkService;
import bisq.network.NetworkServiceConfigFactory;
import bisq.offer.OfferBookService;
import bisq.offer.OpenOfferService;
import bisq.oracle.marketprice.MarketPriceService;
import bisq.oracle.marketprice.MarketPriceServiceConfigFactory;
import bisq.persistence.PersistenceService;
import bisq.protocol.ProtocolService;
import bisq.security.KeyPairService;
import bisq.settings.SettingsService;
import bisq.social.chat.ChatService;
import bisq.social.intent.TradeIntentListingsService;
import bisq.social.intent.TradeIntentService;
import bisq.social.userprofile.UserProfileService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static bisq.common.util.OsUtils.EXIT_FAILURE;
import static bisq.common.util.OsUtils.EXIT_SUCCESS;
import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * Creates domain specific options from program arguments and application options.
 * Creates domain instance with options and optional dependency to other domain objects.
 * Initializes the domain instances according to the requirements of their dependencies either in sequence
 * or in parallel.
 * Provides the completely setup instances to other clients (Api)
 */
@Getter
@Slf4j
public class DefaultApplicationService extends ServiceProvider {
    private final KeyPairService keyPairService;
    private final NetworkService networkService;
    private final OpenOfferService openOfferService;
    private final IdentityService identityService;
    private final MarketPriceService marketPriceService;
    private final ApplicationConfig applicationConfig;
    private final PersistenceService persistenceService;
    private final SettingsService settingsService;
    private final ChatService chatService;
    private final ProtocolService protocolService;
    private final OfferBookService offerBookService;
    private final AccountService accountService;
    private final TradeIntentListingsService tradeIntentListingsService;
    private final TradeIntentService tradeIntentService;
    private final UserProfileService userProfileService;

    public DefaultApplicationService(String[] args) {
        super("Bisq");
        this.applicationConfig = ApplicationConfigFactory.getConfig(getConfig("bisq.application"), args);

        Locale locale = applicationConfig.getLocale();
        LocaleRepository.initialize(locale);
        Res.initialize(locale);

        persistenceService = new PersistenceService(applicationConfig.baseDir());
        keyPairService = new KeyPairService(persistenceService);

        settingsService = new SettingsService(persistenceService);


        NetworkService.Config networkServiceConfig = NetworkServiceConfigFactory.getConfig(applicationConfig.baseDir(),
                getConfig("bisq.networkServiceConfig"));
        networkService = new NetworkService(networkServiceConfig, persistenceService, keyPairService);

        IdentityService.Config identityServiceConfig = IdentityService.Config.from(getConfig("bisq.identityServiceConfig"));
        identityService = new IdentityService(persistenceService, keyPairService, networkService, identityServiceConfig);

        accountService = new AccountService(persistenceService);

        userProfileService = new UserProfileService(persistenceService, keyPairService,identityService);
        chatService = new ChatService(persistenceService, identityService, networkService);
        tradeIntentListingsService = new TradeIntentListingsService(networkService);
        tradeIntentService = new TradeIntentService(networkService, identityService, tradeIntentListingsService, chatService);

        // add data use case is not available yet at networkService
        openOfferService = new OpenOfferService(networkService, identityService, persistenceService);
        offerBookService = new OfferBookService(networkService);

        MarketPriceService.Config marketPriceServiceConf = MarketPriceServiceConfigFactory.getConfig();
        marketPriceService = new MarketPriceService(marketPriceServiceConf, networkService, ApplicationVersion.VERSION);
        // offerPresentationService = new OfferPresentationService(offerService, marketPriceService);

        protocolService = new ProtocolService(networkService, identityService, persistenceService, openOfferService);
    }

    public CompletableFuture<Boolean> readAllPersisted() {
        return persistenceService.readAllPersisted();
    }

    /**
     * Initializes all domain objects, services and repositories.
     * We do in parallel as far as possible. If there are dependencies we chain those as sequence.
     */
    @Override
    public CompletableFuture<Boolean> initialize() {
        return keyPairService.initialize()
                .thenCompose(result -> networkService.bootstrapToNetwork())
                .thenCompose(result -> identityService.initialize())
                .thenCompose(result -> marketPriceService.initialize())
                .whenComplete((list, throwable) -> {
                    // add dummy accounts
                    if (accountService.getAccounts().isEmpty()) {
                        SepaAccount john_smith = new SepaAccount("SEPA-account-1",
                                "John Smith",
                                "iban_1234",
                                "bic_1234",
                                CountryRepository.getDefaultCountry());
                        accountService.addAccount(john_smith);
                        accountService.addAccount(new SepaAccount("SEPA-account-2",
                                "Mary Smith",
                                "iban_5678",
                                "bic_5678",
                                CountryRepository.getDefaultCountry()));
                        accountService.addAccount(new RevolutAccount("revolut-account", "john@gmail.com"));
                    }
                })
                .thenCompose(result -> protocolService.initialize())
                .thenCompose(result -> CompletableFutureUtils.allOf(
                        userProfileService.initialize(),
                        openOfferService.initialize(),
                        offerBookService.initialize(),
                        tradeIntentListingsService.initialize(),
                        tradeIntentService.initialize()))
                .orTimeout(120, TimeUnit.SECONDS)
                .whenComplete((list, throwable) -> {
                    if (throwable != null) {
                        log.error("Error at startup", throwable);
                    } else {
                        log.info("Application initialized successfully");
                    }
                }).thenApply(list -> list.stream().allMatch(e -> e));
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return runAsync(() -> openOfferService.shutdown()
                .thenCompose(list -> {
                    marketPriceService.shutdown();
                    return networkService.shutdown()
                            .whenComplete((__, throwable) -> {
                                if (throwable != null) {
                                    log.error("Error at shutdown", throwable);
                                    System.exit(EXIT_FAILURE);
                                } else {
                                    // In case the application is a JavaFXApplication give it chance to trigger the exit
                                    // via Platform.exit()
                                    runAsync(() -> System.exit(EXIT_SUCCESS));
                                }
                            });
                }), ExecutorFactory.newSingleThreadExecutor("Shutdown"));
    }
}