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

package listening.linuxsuren.github.io.componet;

import listening.linuxsuren.github.io.service.CollectionService;
import listening.linuxsuren.github.io.service.LocalProfileService;
import listening.linuxsuren.github.io.service.Podcast;
import listening.linuxsuren.github.io.service.Profile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a panel for exploring Podcasts
 */
public class ExplorePanel extends JPanel {
    private List<PodcastEvent> podcastEventList = new ArrayList<>();
    private CollectionService collectionService;

    public ExplorePanel() {
        this.setName("Explore");

        this.setLayout(new GridLayout(0, 3, 10, 10));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                reload();
            }
        });
    }

    public void reload() {
        if (collectionService != null) {
            load(collectionService);
        }
    }

    public void load(CollectionService collectionService) {
        this.collectionService = collectionService;

        List<Podcast> allPodcasts = collectionService.getAll();

        try {
            Profile profile = new LocalProfileService().getProfile();
            List<Podcast> personalPodcasts = profile.getPersonalPodcasts();
            if (personalPodcasts != null && !personalPodcasts.isEmpty()) {
                allPodcasts.addAll(personalPodcasts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        allPodcasts.stream().distinct().forEach((podcast) -> {
            boolean exists = Arrays.stream(getComponents()).anyMatch((c) -> c.getName().equals(podcast.getName()));
            if (exists) {
                return;
            }

            CollectionCardPanel panel = new CollectionCardPanel(podcast);
            panel.setName(podcast.getName());
            panel.asyncLoad(collectionService);
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        podcastEventList.forEach((p) -> {
                            p.trigger(podcast);
                        });
                    }
                }
            });
            add(panel);
        });
    }

    public void addEvent(PodcastEvent e) {
        podcastEventList.add(e);
    }
}
