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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.apache.commons.io.Charsets;
import uk.co.thinkofdeath.thinkcraft.bukkit.ThinkMapPlugin;

import java.net.URI;

import static io.netty.handler.codec.http.HttpMethod.OPTIONS;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ChunkEndPoint extends EndPoint {

    private final ThinkMapPlugin plugin;

    public ChunkEndPoint(ThinkMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(ChannelHandlerContext context, URI uri, FullHttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, context.alloc().buffer());
        response.headers().add("Access-Control-Allow-Origin", "*");
        response.headers().add("Access-Control-Allow-Methods", "POST");
        if (request.getMethod() == OPTIONS) {
            response.headers().add("Access-Control-Allow-Headers", "origin, content-type, accept");
        }

        if (request.getMethod() == POST) {
            String[] args = request.content().toString(Charsets.UTF_8).split(":");
            ByteBuf out = response.content();
            if (plugin.getChunkManager(plugin.getTargetWorld()).getChunkBytes(Integer.parseInt(args[0]), Integer.parseInt(args[1]), out)) {
                response.headers().add("Content-Encoding", "gzip");
            } else {
                out.writeBytes(new byte[1]);
            }
        }
        sendHttpResponse(context, request, response);
    }
}
