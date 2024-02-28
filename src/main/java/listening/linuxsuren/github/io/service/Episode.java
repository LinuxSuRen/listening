/*
Copyright 2024 LinuxSuRen.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package listening.linuxsuren.github.io.service;

import java.time.Duration;
import java.time.ZonedDateTime;

public class Episode {
    private String podcast;
    private String title;
    private String audioURL;
    private long length;
    private String mediaType;
    private String htmlNote;
    private String rssURL;
    private String link;
    private ZonedDateTime publishDate;
    private Duration duration;

    public Episode() {}

    public Episode(String title) {
        this.title = title;
    }

    public String getPodcast() {
        return podcast;
    }

    public void setPodcast(String podcast) {
        this.podcast = podcast;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getHtmlNote() {
        String note = htmlNote.replaceAll("(\\d\\d):(\\d\\d):(\\d\\d)", "<a href=\"$1h:$2h:$3s\">$1:$2:$3</a>");
        if (note.equals(htmlNote)) {
            note = htmlNote.replaceAll("(\\d\\d):(\\d\\d)", "<a href=\"$1m:$2s\">$1:$2</a>");
        }
        return note;
   }

    public void setHtmlNote(String htmlNote) {
        this.htmlNote = htmlNote;
    }

    public String getRssURL() {
        return rssURL;
    }

    public void setRssURL(String rssURL) {
        this.rssURL = rssURL;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
