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

import org.reactivestreams.Publisher
import org.reactivestreams.tck.PublisherVerification
import org.reactivestreams.tck.TestEnvironment
import ratpack.func.Action
import ratpack.stream.Streams

class WiretapPublisherVerification extends PublisherVerification<Integer> {

  public static final long DEFAULT_TIMEOUT_MILLIS = 300L
  public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L

  WiretapPublisherVerification() {
    super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS)
  }

  @Override
  Publisher<Integer> createPublisher(long elements) {
    Streams.wiretap(Streams.publish(1..elements), Action.noop())
  }
  long maxElementsFromPublisher() {
    1000
  }


  @Override
  Publisher<Integer> createFailedPublisher() {
    return null
  }
}
