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

package bisq.desktop.primary.main.content.newProfilePopup.selectUserType;

import bisq.desktop.common.view.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import lombok.Getter;

@Getter
public class SelectUserTypeModel implements Model {
    public enum Type {
        NEWBIE,
        EXPERIENCED
    }

    private final ObjectProperty<Type> selectedType = new SimpleObjectProperty<>();
    private final String profileId;
    private final Image roboHashNode;

    public SelectUserTypeModel(String profileId, Image roboHashNode) {
        this.profileId = profileId;
        this.roboHashNode = roboHashNode;
    }

    void setSelectedType(Type type) {
        selectedType.set(type);
    }
}