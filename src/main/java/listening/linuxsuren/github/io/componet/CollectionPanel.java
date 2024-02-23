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

import listening.linuxsuren.github.io.filter.EpisodeFilter;
import listening.linuxsuren.github.io.filter.EpisodeNonFilter;
import listening.linuxsuren.github.io.filter.EpisodeTitleFilter;
import listening.linuxsuren.github.io.service.CollectionService;
import listening.linuxsuren.github.io.service.Podcast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Podcast program
 */
public class CollectionPanel extends JPanel {
    private CollectionService collectionService;
    private Podcast podcast;
    private JPanel episodeListPanel = new JPanel();
    private List<EpisodeEvent> eventList = new ArrayList<>();

    public CollectionPanel(CollectionService collectionService) {
        this.collectionService = collectionService;
        episodeListPanel.setLayout(new GridLayout(0, 1));

        this.setLayout(new BorderLayout());
        this.add(episodeListPanel, BorderLayout.CENTER);
        this.add(createToolBar(), BorderLayout.NORTH);
    }

    private JPanel createToolBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel searchLabel = new JLabel("Search: ");
        JTextField searchField = new JTextField(15);
        searchField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPodcast(podcast, new EpisodeTitleFilter(e.getActionCommand()));
            }
        });

        panel.add(searchLabel);
        panel.add(searchField);
        return panel;
    }

    public void loadPodcast(Podcast podcast) {
        loadPodcast(podcast, new EpisodeNonFilter());
    }

    public void loadPodcast(Podcast podcast, final EpisodeFilter filter) {
        this.podcast = podcast;
        episodeListPanel.removeAll();

        new Thread(() -> {
            collectionService.getEpisode(podcast).forEach((i) -> {
                if (!filter.match(i)) {
                    return;
                }

                JLabel label = new JLabel(i.getTitle());
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        eventList.forEach((episodeEvent -> {
                            episodeEvent.trigger(i);
                        }));
                    }
                });

                JPanel panel = new JPanel();
                panel.add(label);

                episodeListPanel.add(panel);

                repaint();
                revalidate();
            });
        }).start();
    }

    public void addEvent(EpisodeEvent e) {
        eventList.add(e);
    }
}
