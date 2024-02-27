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
import listening.linuxsuren.github.io.server.CacheServer;
import listening.linuxsuren.github.io.service.*;

import javax.swing.*;
import java.awt.*;

/**
 * This is the main panel which is the container of all others
 */
public class MainPanel extends JPanel {
    private BreadCrumbPanel breadCrumbPanel;
    private CardLayout cardLayout = new CardLayout();
    private JFXPanel fxPanel = new JFXPanel();
    private Player player;
    private ExplorePanel explorePanel;
    private CollectionService collectionService = new SimpleCollectionService();

    public MainPanel() {
        this.setLayout(new BorderLayout());

        JPanel centerPanel = createCenterPanel();

        breadCrumbPanel = new BreadCrumbPanel(centerPanel, cardLayout);
        breadCrumbPanel.append(explorePanel);

        Platform.runLater(this::addPlayer);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(fxPanel, BorderLayout.NORTH);
        this.add(breadCrumbPanel, BorderLayout.SOUTH);
    }
    
    private void addPlayer() {
        String lastDuration = "";
        Episode episode = new Episode();
        episode.setAudioURL("https://fake");
        try {
            Profile profile = new LocalProfileService().getProfile();
            episode.setAudioURL(CacheServer.wrap(profile.getCurrentEpisode().getAudioURL()));
            lastDuration = profile.getCurrentEpisode().getDuration() + "ms";
            episode.setTitle(profile.getCurrentEpisode().getEpisode());
            episode.setPodcast(profile.getCurrentEpisode().getPodcast());
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
        explorePanel.load(collectionService);
        explorePanel.addEvent((e) -> {
            CollectionPanel panel = new CollectionPanel(collectionService);
            panel.loadPodcast(e);
            panel.addEvent((ee) -> {
                EpisodePanel episodePanel = new EpisodePanel(ee);
                episodePanel.setPlayEvent(player);
                breadCrumbPanel.append(episodePanel);
            });

            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setName(e.getName());
            breadCrumbPanel.append(scrollPane);
        });

        return centerPanel;
    }
}
