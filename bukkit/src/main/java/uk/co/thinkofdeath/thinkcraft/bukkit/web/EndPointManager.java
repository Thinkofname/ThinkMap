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

package uk.co.thinkofdeath.thinkcraft.bukkit.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EndPointManager {

    private final HashMap<String, EndPoint> endPoints = new HashMap<>();
    private final HashMap<Pattern, EndPoint> specialPoints = new HashMap<>();
    private EndPoint def;

    public void handle(ChannelHandlerContext context, URI uri, FullHttpRequest request) throws Exception {
        if (endPoints.containsKey(uri.getPath())) {
            endPoints.get(uri.getPath()).handle(context, uri, request);
            return;
        }
        for (Map.Entry<Pattern, EndPoint> e : specialPoints.entrySet()) {
            if (e.getKey().matcher(uri.getPath()).find()) {
                e.getValue().handle(context, uri, request);
                return;
            }
        }
        if (def != null) {
            def.handle(context, uri, request);
        } else {
            HTTPHandler.sendHttpResponse(context, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND)
            );
        }
    }

    public void add(String path, EndPoint endPoint) {
        endPoints.put(path, endPoint);
    }

    public void add(Pattern pattern, EndPoint endPoint) {
        specialPoints.put(pattern, endPoint);
    }

    public void setDefault(EndPoint def) {
        this.def = def;
    }
}
