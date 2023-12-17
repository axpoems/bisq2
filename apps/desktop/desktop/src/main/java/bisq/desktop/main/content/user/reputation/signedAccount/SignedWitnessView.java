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

package bisq.desktop.main.content.user.reputation.signedAccount;

import bisq.bisq_easy.NavigationTarget;
import bisq.desktop.DesktopModel;
import bisq.desktop.common.Styles;
import bisq.desktop.common.view.Controller;
import bisq.desktop.common.view.Model;
import bisq.desktop.common.view.TabView;
import bisq.desktop.common.view.View;
import bisq.desktop.components.containers.Spacer;
import bisq.desktop.components.controls.BisqIconButton;
import bisq.desktop.overlay.OverlayModel;
import bisq.i18n.Res;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignedWitnessView extends TabView<SignedWitnessModel, SignedWitnessController> {
    private Button closeButton;

    public SignedWitnessView(SignedWitnessModel model, SignedWitnessController controller) {
        super(model, controller);

        double width = DesktopModel.MIN_WIDTH - 20;
        root.setMinWidth(width);
        root.setMaxWidth(width);
        double height = DesktopModel.MIN_HEIGHT - 80;
        root.setMinHeight(height);
        root.setMaxHeight(height);

        root.setPadding(new Insets(40, 68, 40, 68));
        root.getStyleClass().add("popup-bg");

        topBox.setPadding(new Insets(0, 0, 0, 0));
        lineAndMarker.setPadding(new Insets(0, 0, 0, 0));

        Styles styles = new Styles("bisq-text-grey-9", "bisq-text-white", "bisq-text-green", "bisq-text-grey-9");
        addTab(Res.get("user.reputation.signedWitness.tab1"),
                NavigationTarget.SIGNED_WITNESS_TAB_1,
                styles);
        addTab(Res.get("user.reputation.signedWitness.tab2"),
                NavigationTarget.SIGNED_WITNESS_TAB_2,
                styles);
        addTab(Res.get("user.reputation.signedWitness.tab3"),
                NavigationTarget.SIGNED_WITNESS_TAB_3,
                styles);
    }

    @Override
    protected void onViewAttached() {
        line.prefWidthProperty().unbind();
        line.prefWidthProperty().bind(root.widthProperty().subtract(136));
        closeButton.setOnAction(e -> controller.onClose());

        root.setPrefWidth(OverlayModel.WIDTH);
        root.setPrefHeight(OverlayModel.HEIGHT + 10);
    }

    @Override
    protected void onViewDetached() {
        line.prefWidthProperty().unbind();
        closeButton.setOnAction(null);
    }

    @Override
    protected void setupTopBox() {
        headline = new Label();
        headline.getStyleClass().add("bisq-text-17");

        closeButton = BisqIconButton.createIconButton("close");

        headline.setText(Res.get("user.reputation.signedWitness"));

        HBox.setMargin(headline, new Insets(0, 0, 0, -2));
        HBox hBox = new HBox(headline, Spacer.fillHBox(), closeButton);

        tabs.setFillHeight(true);
        tabs.setSpacing(46);
        tabs.setMinHeight(35);

        topBox = new VBox();
        VBox.setMargin(hBox, new Insets(0, 0, 17, 0));
        topBox.getChildren().addAll(hBox, tabs);
    }

    @Override
    protected void setupLineAndMarker() {
        super.setupLineAndMarker();

        line.getStyleClass().remove("bisq-dark-bg");
        line.getStyleClass().add("bisq-mid-grey");
    }

    @Override
    protected void onChildView(View<? extends Parent, ? extends Model, ? extends Controller> oldValue,
                               View<? extends Parent, ? extends Model, ? extends Controller> newValue) {
        super.onChildView(oldValue, newValue);
        scrollPane.setFitToHeight(true);
    }
}
