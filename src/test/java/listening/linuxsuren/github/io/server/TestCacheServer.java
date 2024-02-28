/*
 * Copyright 2024 LinuxSuRen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package listening.linuxsuren.github.io.server;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.Assert.*;

public class TestCacheServer {
    @Test
    public void parseCacheLive() {
        assertNull(CacheServer.parseCacheLive("name=rick"));

        Duration duration = CacheServer.parseCacheLive("cacheLive=PT1h");
        assertNotNull(duration);
        assertEquals(1, duration.toHours());
    }

    @Test
    public void wrapURLWithLive() throws MalformedURLException {
        URL urlWithoutLive = CacheServer.wrapURLWithLive("https://github.com", null);
        assertEquals("https://github.com", urlWithoutLive.toString());

        URL urlWithLive = CacheServer.wrapURLWithLive("https://github.com", Duration.ofHours(1));
        assertEquals("https://github.com?cacheLive=PT1H", urlWithLive.toString());
    }
}
