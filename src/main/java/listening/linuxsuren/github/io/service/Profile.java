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

import java.util.List;

public class Profile {
    private int volume;
    private ToDoEpisode currentEpisode;
    private List<ToDoEpisode> episodes;
    private List<Podcast> personalPodcasts;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public ToDoEpisode getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(ToDoEpisode currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public List<ToDoEpisode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<ToDoEpisode> episodes) {
        this.episodes = episodes;
    }

    public List<Podcast> getPersonalPodcasts() {
        return personalPodcasts;
    }

    public void setPersonalPodcasts(List<Podcast> personalPodcasts) {
        this.personalPodcasts = personalPodcasts;
    }
}
