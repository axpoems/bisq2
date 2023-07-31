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

package bisq.tor.bootstrap;

import net.freehaven.tor.control.EventHandler;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class BootstrapEventHandler implements EventHandler {

    private final Set<BootstrapEventListener> listeners = new CopyOnWriteArraySet<>();

    public void addListener(BootstrapEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BootstrapEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void circuitStatus(String status, String circID, String path) {
    }

    @Override
    public void streamStatus(String status, String streamID, String target) {
    }

    @Override
    public void orConnStatus(String status, String orName) {
    }

    @Override
    public void bandwidthUsed(long read, long written) {
    }

    @Override
    public void newDescriptors(List<String> orList) {
    }

    @Override
    public void message(String severity, String msg) {
    }

    @Override
    public void hiddenServiceEvent(String action, String msg) {
    }

    @Override
    public void hiddenServiceFailedEvent(String reason, String msg) {
    }

    @Override
    public void hiddenServiceDescriptor(String descriptorId, String descriptor, String msg) {
    }

    @Override
    public void unrecognized(String type, String msg) {
        if (BootstrapEvent.isBootstrapMessage(type, msg)) {
            BootstrapEvent bootstrapEvent = BootstrapEvent.fromEventMessage(msg);
            listeners.forEach(l -> l.onBootstrapStatusEvent(bootstrapEvent));
        }
    }

    @Override
    public void timeout() {
    }
}