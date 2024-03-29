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

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.util.Duration;
import listening.linuxsuren.github.io.server.CacheServer;
import listening.linuxsuren.github.io.service.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * This is the main panel which is the container of all others
 */
public class MainPanel extends JPanel {
    private BreadCrumbPanel breadCrumbPanel;
    private CardLayout cardLayout = new CardLayout();
    private JFXPanel fxPanel = new JFXPanel();
    private final JPopupMenu popupMenu;
    private final JComponent rightPanel;
    private Player player;
    private ExplorePanel explorePanel;
    private final AddPodcastDialog addPodcastDialog;
    private CollectionService collectionService = new SimpleCollectionService();

    public MainPanel(JFrame frame) {
        this.setLayout(new BorderLayout());

        JPanel centerPanel = createCenterPanel();
        rightPanel = createRightPanel();
        popupMenu = createPopupMenu();

        JScrollPane exploreScrollPane = new JScrollPane(explorePanel);
        exploreScrollPane.setName(explorePanel.getName());
        breadCrumbPanel = new BreadCrumbPanel(centerPanel, cardLayout);
        breadCrumbPanel.append(exploreScrollPane);

        addPodcastDialog = new AddPodcastDialog(frame);

        Platform.runLater(this::addPlayer);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(fxPanel, BorderLayout.NORTH);
        this.add(rightPanel, BorderLayout.EAST);
        this.add(breadCrumbPanel, BorderLayout.SOUTH);
    }
    
    private void addPlayer() {
        String lastDuration = "";
        Episode episode = new Episode();
        episode.setAudioURL("https://fake");
        try {
            Profile profile = new LocalProfileService().getProfile();
            if (profile != null) {
                ToDoEpisode current = profile.getCurrentEpisode();
                if (current != null) {
                    episode.setAudioURL(CacheServer.wrap(current.getAudioURL()));
                    lastDuration = current.getIndex() + "ms";
                    episode.setTitle(current.getEpisode());
                    episode.setPodcast(current.getPodcast());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lastDuration.isEmpty()) {
                lastDuration = "0ms";
            }
        }
        
        player = new Player(episode);
        player.seek(lastDuration);
        player.setTitleLabel(episode.getTitle());
        Scene scene = new Scene(player, javafx.scene.paint.Color.ALICEBLUE);
        fxPanel.setScene(scene);
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(cardLayout);

        explorePanel = new ExplorePanel();
        BorderUtil.setInsideBorder(explorePanel, 10);
        new Thread(() -> {
            explorePanel.load(collectionService);
        }).start();
        explorePanel.addEvent((e) -> {
            CollectionPanel panel = new CollectionPanel(collectionService);
            panel.loadPodcast(e);
            panel.addEvent(showEpisode);

            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setName(e.getName());
            breadCrumbPanel.append(scrollPane);
        });
        explorePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        return centerPanel;
    }

    private JComponent createRightPanel() {
        TodoListPanel panel = new TodoListPanel((episode) -> {
            Episode rawEpisode = episode.toEpisode();
            EpisodePanel episodePanel = new EpisodePanel(rawEpisode);
            episodePanel.setPlayEvent(player);

            player.play(rawEpisode);
            player.seek(new Duration(episode.getIndex()));
            player.setTitleLabel(rawEpisode.getTitle());

            breadCrumbPanel.append(episodePanel);
        });

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVisible(false);
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                scrollPane.getParent().revalidate();
                panel.reload();
            }
        });
        return scrollPane;
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JCheckBoxMenuItem laterMenu = new JCheckBoxMenuItem("Later");
        JMenuItem reloadMenu = new JMenuItem("Reload");
        JMenuItem openConfigMenu = new JMenuItem("Open Config");
        JMenuItem addRssMenu = new JMenuItem("Add RSS");
        JMenuItem recentEpisodeMenu = new JMenuItem("Recent Episodes");
        laterMenu.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPanel.setVisible(laterMenu.isSelected());
                rightPanel.getParent().revalidate();
            }
        });
        reloadMenu.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                explorePanel.reload();
            }
        });
        openConfigMenu.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(new File(System.getProperty("user.home"), ".config/listening"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addRssMenu.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPodcastDialog.setVisible(true);
            }
        });
        recentEpisodeMenu.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RecentEpisodePanel recentEpisodePanel = new RecentEpisodePanel();
                recentEpisodePanel.reload();
                recentEpisodePanel.addEvent(showEpisode);

                ScrollPane recentEpisodeScrollPanel = new ScrollPane();
                recentEpisodeScrollPanel.add(recentEpisodePanel);
                recentEpisodeScrollPanel.setName("Recent");

                breadCrumbPanel.append(recentEpisodeScrollPanel);
            }
        });

        popupMenu.add(laterMenu);
        popupMenu.add(reloadMenu);
        popupMenu.add(openConfigMenu);
        popupMenu.add(addRssMenu);
        popupMenu.add(recentEpisodeMenu);
        return popupMenu;
    }

    private EpisodeEvent showEpisode = ((Episode episode) -> {
        EpisodePanel episodePanel = new EpisodePanel(episode);
        episodePanel.setPlayEvent(player);
        breadCrumbPanel.append(episodePanel);
    });
}
