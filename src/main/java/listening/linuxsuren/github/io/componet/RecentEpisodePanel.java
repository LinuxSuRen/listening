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

import listening.linuxsuren.github.io.service.Episode;
import listening.linuxsuren.github.io.service.SimpleCollectionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecentEpisodePanel extends JPanel implements ReloadAble {
    private final JComboBox<RecentType> recentBox = new JComboBox<>();
    private final JPanel centerPanel = new JPanel();
    private List<EpisodeEvent> eventList = new ArrayList<>();

    public RecentEpisodePanel() {
        JPanel toolbar = createToolbar();

        // set the center panel
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        this.setLayout(new BorderLayout());
        this.add(toolbar, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createToolbar() {
        recentBox.addItem(RecentType.Week);
        recentBox.addItem(RecentType.BiWeek);
        recentBox.addItem(RecentType.Month);
        recentBox.addItemListener((e) -> reload());

        JPanel panel = new JPanel();
        panel.add(recentBox);
        return panel;
    }

    @Override
    public void reload() {
        centerPanel.removeAll();

        new Thread(() -> {
            SimpleCollectionService service = new SimpleCollectionService();

            RecentType recentType = (RecentType) recentBox.getSelectedItem();
            final ZonedDateTime expectedRange =
                    ZonedDateTime.now().minusDays(recentType == null ? RecentType.Week.getDays() : recentType.getDays());
            service.getAll().forEach(podcast -> {
                service.getEpisode(podcast).stream().filter((e) -> e.getPublishDate().isAfter(expectedRange)).
                        forEach(episode -> {
                    EpisodeCard card = new EpisodeCard(episode);
                    card.addTrigger(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            eventList.forEach((episodeEvent -> {
                                episodeEvent.trigger(episode);
                            }));
                        }
                    });
                    centerPanel.add(card);
                });

                revalidate();
            });
        }).start();
    }

    public void addEvent(EpisodeEvent e) {
        eventList.add(e);
    }
}

class EpisodeCard extends JPanel {
    private final JLabel logo = new JLabel();

    public EpisodeCard(Episode episode) {
        JLabel title = new JLabel();
        title.setText(episode.getTitle());
        BorderUtil.setInsideBorder(title, 10);

        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logo.setIcon(CachedImage.ScaledImageIcon(episode.getLogoURL()));

        this.setLayout(new BorderLayout());
        BorderUtil.setInsideBorder(this, 10);
        this.add(logo, BorderLayout.WEST);
        this.add(title, BorderLayout.CENTER);
    }

    public void addTrigger(MouseAdapter e) {
        logo.addMouseListener(e);
    }
}

enum RecentType {
    Week, BiWeek, Month;

    int getDays() {
        switch (this) {
            case Week: return 7;
            case BiWeek: return 14;
            default: return 30;
        }
    }
}
