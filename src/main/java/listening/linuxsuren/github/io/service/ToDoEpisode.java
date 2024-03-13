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

public class ToDoEpisode {
    private String podcast;
    private String episode;
    private String audioURL;
    private String rssURL;
    private double index;
    private double duration;

    public static ToDoEpisode ofEpisode(Episode episode) {
        ToDoEpisode todoEpisode = new ToDoEpisode();
        todoEpisode.setPodcast(episode.getPodcast());
        todoEpisode.setEpisode(episode.getTitle());
        todoEpisode.setAudioURL(episode.getAudioURL());
        todoEpisode.setRssURL(episode.getRssURL());
        if (episode.getDuration() != null) {
            todoEpisode.setDuration(episode.getDuration().toMillis());
        }
        return todoEpisode;
    }

    public Episode toEpisode() {
        Episode episode = new Episode();
        episode.setPodcast(this.getPodcast());
        episode.setTitle(this.getEpisode());
        episode.setAudioURL(this.getAudioURL());
        episode.setRssURL(this.getRssURL());
        return episode;
    }

    public String getPodcast() {
        return podcast;
    }

    public void setPodcast(String podcast) {
        this.podcast = podcast;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public String getRssURL() {
        return rssURL;
    }

    public void setRssURL(String rssURL) {
        this.rssURL = rssURL;
    }

    public double getIndex() {
        return index;
    }

    public void setIndex(double index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        if (audioURL == null) {
            return 0;
        }
        return audioURL.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return hashCode() == obj.hashCode();
    }
}
