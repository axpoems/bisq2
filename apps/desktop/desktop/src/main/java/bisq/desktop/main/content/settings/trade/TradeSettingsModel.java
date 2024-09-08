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

package bisq.desktop.main.content.settings.trade;

import bisq.desktop.common.view.Model;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class TradeSettingsModel implements Model {
    private final LongProperty minRequiredReputationScore = new SimpleLongProperty();
    private final BooleanProperty minRequiredReputationScoreEditable = new SimpleBooleanProperty();
    private final StringProperty minRequiredReputationScoreDescriptionText = new SimpleStringProperty();
    private final BooleanProperty ignoreMinRequiredReputationScoreFromSecManager = new SimpleBooleanProperty();
    private final BooleanProperty offerOnly = new SimpleBooleanProperty();
    private final BooleanProperty closeMyOfferWhenTaken = new SimpleBooleanProperty();
    private final DoubleProperty maxTradePriceDeviation = new SimpleDoubleProperty();

    public TradeSettingsModel() {
    }
}