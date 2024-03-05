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
import be.ceau.podcastparser.exceptions.InvalidFeedFormatException;
import be.ceau.podcastparser.models.core.Feed;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleCollectionService implements CollectionService {
    private PodcastIndex index = null;

    public SimpleCollectionService() {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("listening/linuxsuren/github/io/service/index.yaml")) {
            Representer representer = new Representer(new DumperOptions());
            representer.getPropertyUtils().setSkipMissingProperties(true);
            LoaderOptions loaderOptions = new LoaderOptions();
            Yaml yaml = new Yaml(new Constructor(PodcastIndex.class, loaderOptions), representer);

            index = yaml.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            if (index == null || index.getIndexServer() == null || index.getIndexServer().isEmpty()) {
                return;
            }

            HttpRequest request = null;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(index.getIndexServer()))
                        .GET()
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

            try {
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() != 200) {
                    System.out.println("failed to load the index, status code: " + response.statusCode());
                    return;
                }

                try (InputStream input = response.body()) {
                    Representer representer = new Representer(new DumperOptions());
                    representer.getPropertyUtils().setSkipMissingProperties(true);
                    LoaderOptions loaderOptions = new LoaderOptions();
                    Yaml yaml = new Yaml(new Constructor(PodcastIndex.class, loaderOptions), representer);

                    index = yaml.load(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public List<Podcast> getAll() {
        return index.getItems();
    }

    @Override
    public void loadPodcast(Podcast podcast) {
        try {
            URL rssURL = new URL(podcast.getRss());
            Feed feed = new PodcastParser().parse(new InputStreamReader(rssURL.openStream()));

            podcast.setName(feed.getTitle());
            if (feed.getLinks() != null && !feed.getLinks().isEmpty()) {
                podcast.setLink(feed.getLinks().stream().iterator().next().getHref());
            }
            feed.getImages().stream().findFirst().ifPresent(image -> podcast.setLogoURL(image.getUrl()));
            feed.getCategories().forEach(category -> podcast.getCategories().add(category.getName()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (InvalidFeedFormatException e) {
            System.out.println("invalid feed format: " + podcast.getRss());
            throw e;
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

            AtomicInteger index = new AtomicInteger();
            feed.getItems().forEach((i) -> {
                Episode episode = new Episode();
                episode.setPodcast(feed.getTitle());
                episode.setNumber(index.getAndIncrement());
                episode.setTitle(i.getTitle().getText());
                episode.setHtmlNote(i.getDescription().getText());
                episode.setRssURL(rssAddress);
                episode.setDuration(i.getDuration());
                episode.setPublishDate(i.getPubDate());
                if (i.getEnclosure() != null) {
                    episode.setAudioURL(i.getEnclosure().getUrl());
                    episode.setLength(i.getEnclosure().getLength());
                    episode.setMediaType(i.getEnclosure().getType());
                }
                if (i.getLinks() != null && !i.getLinks().isEmpty()) {
                    episode.setLink(i.getLinks().stream().iterator().next().getHref());
                }
                episodes.add(episode);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return episodes;
    }
}
