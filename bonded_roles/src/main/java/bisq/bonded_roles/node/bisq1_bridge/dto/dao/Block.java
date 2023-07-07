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

package bisq.bonded_roles.node.bisq1_bridge.dto.dao;

import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

@Data
public final class Block {
    private List<Tx> txs;
    private int height;
    private long time; // in ms
    private String hash;
    @Nullable // in case of first block in the blockchain
    private String previousBlockHash;
}