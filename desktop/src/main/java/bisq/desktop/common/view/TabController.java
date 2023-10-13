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

package bisq.desktop.common.view;

import bisq.chat.ChatChannelDomain;
import bisq.chat.notifications.ChatNotificationService;
import bisq.desktop.ServiceProvider;
import bisq.desktop.common.threading.UIThread;
import bisq.presentation.notifications.NotificationsService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class TabController<T extends TabModel> extends NavigationController {
    @Getter
    protected final T model;
    private final NotificationsService notificationsService;

    public TabController(T model, NavigationTarget host, ServiceProvider serviceProvider) {
        super(host);

        this.model = model;
        this.notificationsService = serviceProvider.getNotificationsService();
    }

    @Override
    public void onActivateInternal() {
        super.onActivateInternal();

        onTabSelected(model.getNavigationTarget());
        notificationsService.subscribe(e -> {
            UIThread.run(() -> {
                Set<String> tradeIdSet = notificationsService.getNotConsumedNotificationIds().stream()
                        .filter(id -> ChatNotificationService.getChatChannelDomain(id) == ChatChannelDomain.BISQ_EASY_OPEN_TRADES)
                        .collect(Collectors.toSet());
                boolean isNotificationVisible = !tradeIdSet.isEmpty() && !notificationsService.isNotificationDismissed()
                        && Navigation.getCurrentNavigationTarget().get() != NavigationTarget.BISQ_EASY_OPEN_TRADES;
                model.getIsNotificationVisible().set(isNotificationVisible);
            });
        });
    }

    @Override
    protected void onNavigationTargetApplied(NavigationTarget navigationTarget, Optional<Object> data) {
        if (model.getSelectedTabButton().get() != null &&
                navigationTarget != model.getSelectedTabButton().get().getNavigationTarget()) {
            onTabSelected(navigationTarget);
        }
    }

    void onTabSelected(NavigationTarget navigationTarget) {
        findTabButton(navigationTarget).ifPresent(tabButton -> {
            model.getSelectedTabButton().set(tabButton);
            Navigation.navigateTo(navigationTarget);
        });
    }

    void onTabButtonCreated(TabButton tabButton) {
        model.getTabButtons().add(tabButton);
    }

    void onTabButtonRemoved(TabButton tabButton) {
        model.getTabButtons().remove(tabButton);
        if (model.getSelectedTabButton().get().getNavigationTarget() == tabButton.getNavigationTarget()) {
            model.getSelectedTabButton().set(null);
        }
    }

    Optional<TabButton> findTabButton(NavigationTarget navigationTarget) {
        return model.getTabButtons().stream()
                .filter(tabButton -> navigationTarget == tabButton.getNavigationTarget())
                .findAny();
    }
}
