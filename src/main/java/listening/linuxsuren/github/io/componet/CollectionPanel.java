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
import listening.linuxsuren.github.io.service.Episode;
import listening.linuxsuren.github.io.service.Podcast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.List;

/**
 * Represents a Podcast program
 */
public class CollectionPanel extends JPanel {
    private CollectionService collectionService;
    private Podcast podcast;
    private JPanel episodeListPanel = new JPanel();
    private final JComboBox<Integer> yearBox = new JComboBox<>();
    private final JTextField searchField = new JTextField(15);;
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
        searchField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPodcast(podcast);
            }
        });

        JComboBox<String> orderBox = new JComboBox<>();
        orderBox.addItem("Descend");
        orderBox.addItem("Ascend");
        orderBox.addItemListener((ItemEvent e) -> {
            if (e.getItem().toString().equalsIgnoreCase("descend")) {
                episodeComparator = descendOrder;
            } else {
                episodeComparator = ascendOrder;
            }
            loadPodcast(null);
        });
        yearBox.addItemListener((e) -> {
            loadPodcast(null);
        });

        panel.add(new JLabel("Year:"));
        panel.add(yearBox);
        panel.add(orderBox);
        panel.add(searchLabel);
        panel.add(searchField);
        return panel;
    }

    private final Comparator<Episode> ascendOrder = Comparator.comparing(Episode::getPublishDate);
    private final Comparator<Episode> descendOrder = (Episode o1, Episode o2) -> o2.getPublishDate().compareTo(o1.getPublishDate());
    private Comparator<Episode> episodeComparator = descendOrder;

    public void loadPodcast(Podcast podcast) {
        Podcast pc = podcast;
        if (pc == null) {
            pc = this.podcast;
        }
        loadPodcast(pc, new EpisodeTitleFilter(searchField.getText()));
    }

    public void loadPodcast(Podcast podcast, final EpisodeFilter filter) {
        if (podcast == null && this.podcast == null) {
            return;
        }

        Podcast pc;
        if (podcast != null) {
            this.podcast = podcast;
            pc = podcast;
        } else {
            pc = this.podcast;
        }
        episodeListPanel.removeAll();

        new Thread(() -> {
            Map<Integer, String> allYears = new HashMap<>();
            final Object year = yearBox.getSelectedItem();

            collectionService.getEpisode(pc).stream().sorted(episodeComparator).forEach((i) -> {
                if (!filter.match(i)) {
                    return;
                }

                if (year instanceof Integer) {
                    if (i.getPublishDate().getYear() != (Integer) year) {
                        return;
                    }
                }

                allYears.put(i.getPublishDate().getYear(), null);
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
                panel.add(new JLabel(i.getPublishDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))));

                episodeListPanel.add(panel);

                repaint();
                revalidate();
            });

            if (yearBox.getItemCount() == 0) {
                yearBox.addItem(null);
                allYears.keySet().forEach(yearBox::addItem);
            }
        }).start();
    }

    public void addEvent(EpisodeEvent e) {
        eventList.add(e);
    }
}
