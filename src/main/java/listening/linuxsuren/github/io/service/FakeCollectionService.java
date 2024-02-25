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

import be.ceau.podcastparser.PodcastParser;
import be.ceau.podcastparser.models.core.Feed;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FakeCollectionService implements CollectionService {
    @Override
    public List<Podcast> getAll() {
        List<Podcast> podcasts = new ArrayList<>();
        podcasts.add(new Podcast("开源面对面", "https://feeds.osf2f.net/osf2f.xml"));
        podcasts.add(new Podcast("刀熊读乐乐", "https://www.ximalaya.com/album/38346992.xml"));
        podcasts.add(new Podcast("创业内幕", "https://www.ximalaya.com/album/20119986.xml"));
        podcasts.add(new Podcast("真相壁炉", "https://www.ximalaya.com/album/75738459.xml"));
        podcasts.add(new Podcast("历史剥壳", "http://www.ximalaya.com/album/24355721.xml"));
        podcasts.add(new Podcast("KubeSphere Talk", "https://feed.xyzfm.space/nxmnjxgmu6r7"));
        return podcasts;
    }

    @Override
    public void loadPodcast(Podcast podcast) {
        try {
            URL rssURL = new URL(podcast.getRss());
            Feed feed = new PodcastParser().parse(new InputStreamReader(rssURL.openStream()));

            feed.getImages().stream().findFirst().ifPresent(image -> podcast.setLogoURL(image.getUrl()));
            feed.getCategories().forEach(category -> podcast.getCategories().add(category.getName()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Episode> getEpisode(Podcast podcast) {
        return parse(podcast.getRss());
    }

    public List<Episode> parse(String rssAddress) {
        List<Episode> episodes = new ArrayList<>();

        try {
            URL rssURL = new URL(rssAddress);
            Feed feed = new PodcastParser().parse(new InputStreamReader(rssURL.openStream()));

            feed.getItems().forEach((i) -> {
                Episode episode = new Episode();
                episode.setTitle(i.getTitle().getText());
                episode.setHtmlNote(i.getDescription().getText());
                episode.setRssURL(rssAddress);
                episode.setDuration(i.getDuration());
                if (i.getEnclosure() != null) {
                    episode.setAudioURL(i.getEnclosure().getUrl());
                    episode.setLength(i.getEnclosure().getLength());
                    episode.setMediaType(i.getEnclosure().getType());
                }
                episodes.add(episode);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return episodes;
    }
}
