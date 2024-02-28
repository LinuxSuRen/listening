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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a panel for exploring Podcasts
 */
public class ExplorePanel extends JPanel {
    private List<PodcastEvent> podcastEventList = new ArrayList<>();
    private CollectionService collectionService;

    public ExplorePanel() {
        this.setName("Explore");

        this.setLayout(new GridLayout(0, 3));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (collectionService != null) {
                    load(collectionService);
                }
            }
        });
    }

    public void load(CollectionService collectionService) {
        this.collectionService = collectionService;
        this.removeAll();

        collectionService.getAll().forEach((podcast) -> {
            CollectionCardPanel panel = new CollectionCardPanel(podcast);
            panel.asyncLoad(collectionService);
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    podcastEventList.forEach((p) -> {
                        p.trigger(podcast);
                    });
                }
            });
            add(panel);
        });
    }

    public void addEvent(PodcastEvent e) {
        podcastEventList.add(e);
    }
}
