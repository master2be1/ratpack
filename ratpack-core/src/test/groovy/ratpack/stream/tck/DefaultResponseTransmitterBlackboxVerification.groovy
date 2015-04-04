/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.stream.tck

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpResponseStatus
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.tck.SubscriberBlackboxVerification
import org.reactivestreams.tck.TestEnvironment
import ratpack.event.internal.DefaultEventController
import ratpack.func.Function
import ratpack.handling.RequestOutcome
import ratpack.server.internal.DefaultResponseTransmitter

import java.util.concurrent.atomic.AtomicBoolean

import static org.mockito.Matchers.any
import static org.mockito.Mockito.*
import static ratpack.stream.Streams.constant
import static ratpack.stream.Streams.publish

class DefaultResponseTransmitterBlackboxVerification extends SubscriberBlackboxVerification<Integer> {

  public static final long DEFAULT_TIMEOUT_MILLIS = 3000L

  public DefaultResponseTransmitterBlackboxVerification() {
    super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS))
  }

  @Override
  Subscriber<Integer> createSubscriber() {
    ChannelFuture channelFuture = mock(ChannelFuture)

    Channel channel = mock(Channel, RETURNS_DEEP_STUBS)
    when(channel.isOpen()).thenReturn(true)
    when(channel.writeAndFlush(any()).addListener(any())).thenReturn(channelFuture)
    when(channel.isWritable()).thenReturn(true)

    FullHttpRequest nettyRequest = mock(FullHttpRequest, RETURNS_DEEP_STUBS)
    when(nettyRequest.retain()).thenReturn(nettyRequest)
    when(nettyRequest.headers().get(any())).thenReturn(null)
    when(nettyRequest.protocolVersion().isKeepAliveDefault()).thenReturn(false)

    HttpHeaders responseHeaders = mock(HttpHeaders)

    DefaultEventController<RequestOutcome> eventController = mock(DefaultEventController)
    when(eventController.hasListeners).thenReturn(false)

    new DefaultResponseTransmitter(
      new AtomicBoolean(), null, channel, nettyRequest, null, responseHeaders, eventController
    ).transmitter(HttpResponseStatus.OK)
  }

  @Override
  Publisher<ByteBuf> createHelperPublisher(long elements) {
    if (elements == Long.MAX_VALUE) {
      constant(1).map(integerToByteBuf)
    } else if (elements > 0) {
      publish(0..<elements).map(integerToByteBuf)
    } else {
      publish([])
    }
  }

  @Override
  Integer createElement(int element) {
    element
  }

  def integerToByteBuf = new Function<Integer, ByteBuf>() {
    @Override
    ByteBuf apply(Integer i) throws Exception {
      Unpooled.wrappedBuffer([i.byteValue()] as byte[])
    }
  }

}
