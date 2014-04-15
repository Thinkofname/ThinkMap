/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.mapviewer.client.network;

public interface ConnectionHandler {

    /**
     * Called when the server sends a message syncing its time with the clients
     *
     * @param currentTime
     *         The current time of the server. (0 - 23999)
     */
    public void onTimeUpdate(int currentTime);

    /**
     * Called when the server wants to change the position of the client
     *
     * @param x
     *         The position on the x axis
     * @param y
     *         The position on the y axis
     * @param z
     *         The position on the z axis
     */
    public void onSetPosition(int x, int y, int z);

    /**
     * Called when the server sends a message to the client
     *
     * @param message
     *         The message
     */
    public void onMessage(String message);
}
