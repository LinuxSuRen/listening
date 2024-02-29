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

package listening.linuxsuren.github.io.service;

import java.time.Duration;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestToDoEpisode {
    @Test
    public void equals() {
        // empty object
        assertTrue(new ToDoEpisode().equals(new ToDoEpisode()));

        ToDoEpisode toDoEpisodeA = new ToDoEpisode();
        toDoEpisodeA.setAudioURL("https://fake.com");

        ToDoEpisode toDoEpisodeB = new ToDoEpisode();
        toDoEpisodeB.setAudioURL("https://fake.com");
        toDoEpisodeB.setEpisode("fake");
        assertTrue(toDoEpisodeA.equals(toDoEpisodeB));
        assertFalse(toDoEpisodeA.equals(null));
    }

    @Test
    public void ofEpisode() {
        ToDoEpisode episode = ToDoEpisode.ofEpisode(createEpisode());
        assertEquals(createToDoEpisode(), episode);
    }

    @Test
    public void toEpisode() {
        Episode episode = createToDoEpisode().toEpisode();
        assertEquals(createEpisode(), episode);
    }

    private Episode createEpisode() {
        Episode episode = new Episode();
        episode.setPodcast("podcast");
        episode.setTitle("title");
        episode.setAudioURL("audioURL");
        episode.setDuration(Duration.ofSeconds(1));
        return episode;
    }

    private ToDoEpisode createToDoEpisode() {
        ToDoEpisode episode = new ToDoEpisode();
        episode.setEpisode("title");
        episode.setPodcast("podcast");
        episode.setAudioURL("audioURL");
        episode.setDuration(1000);
        return episode;
    }
}
