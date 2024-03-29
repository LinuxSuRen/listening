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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import listening.linuxsuren.github.io.server.CacheServer;
import listening.linuxsuren.github.io.service.Episode;
import listening.linuxsuren.github.io.service.LocalProfileService;
import listening.linuxsuren.github.io.service.ToDoEpisode;

import java.io.IOException;
import java.util.List;

public class Player extends BorderPane implements PlayEvent {
    private Media media;
    private MediaPlayer player;
    private MediaView view;
    private Pane mpane;
    private MediaBar bar;
    private Label titleLabel;

    public Player(Episode episode) {
        media = new Media(episode.getAudioURL());
        player = new MediaPlayer(media);
        view = new MediaView(player);
        mpane = new Pane();
        titleLabel = new Label();
        mpane.getChildren().add(view);
        player.currentTimeProperty().addListener(new PlayerDurationRecord(episode, player));
        player.setOnEndOfMedia(new playNext(this));

        // inorder to add the view
        setCenter(mpane);
        bar = new MediaBar(player);
        bar.setPlayer(player);
        player.setOnReady(() -> {
            player.setStopTime(player.getStopTime());
            bar.reset();
        });
        setBottom(bar);
        setTop(titleLabel);
        setStyle("-fx-background-color:#bfc2c7");
    }

    public void play() {
        player.stop();
        player.setStartTime(new Duration(0));
        player.play();
    }

    public void play(Episode episode) {
        player.stop();
        player.dispose();

        String audioURL = episode.getAudioURL();
        if (audioURL.startsWith("https://") || audioURL.startsWith("http://")) {
            audioURL = CacheServer.wrap(audioURL);
        }
        media = new Media(audioURL);
        player = new MediaPlayer(media);
        bar.setPlayer(player);
        player.setOnReady(() -> {
            player.setStopTime(player.getStopTime());
            bar.reset();
        });
        player.currentTimeProperty().addListener(new PlayerDurationRecord(episode, player));
        player.setOnEndOfMedia(new playNext(this));
        titleLabel.setText(episode.getTitle());
        view.setMediaPlayer(player);
        player.play();
        this.setVisible(true);
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    @Override
    public boolean isPlaying() {
        return player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    @Override
    public void setTitleLabel(String title) {
        Platform.runLater(() -> {
            titleLabel.setText(title);
        });
    }

    @Override
    public void seek(String timeLine) {
        Duration du = null;
        for (String i : timeLine.split(":")) {
            if (i.startsWith("0")) {
                i = i.substring(1);
            }
            if (i.isEmpty() || "ms".equals(i) || "m".equals(i) || "h".equals(i)) {
                continue;
            }
            if (du == null) {
                du = Duration.valueOf(i);
            } else {
                du.add(Duration.valueOf(i));
            }
        }
        if (du != null) {
            seek(du);
        }
    }

    @Override
    public void seek(Duration du) {
        player.setStartTime(du);
        player.seek(du);
        bar.updatesValues();
    }

    static class playNext implements Runnable {
        private final PlayEvent playEvent;

        public playNext(PlayEvent e) {
            this.playEvent = e;
        }

        @Override
        public void run() {
            LocalProfileService service = new LocalProfileService();
            try {
                ToDoEpisode current = service.getProfile().getCurrentEpisode();
                System.out.println(current.getEpisode() + " is finished.");
                service.removeItem(current);

                List<ToDoEpisode> todoList = service.getProfile().getEpisodes();
                if (todoList != null && !todoList.isEmpty()) {
                    Episode next = todoList.get(0).toEpisode();
                    service.setCurrentEpisode(ToDoEpisode.ofEpisode(next));
                    playEvent.play(next);
                } else {
                    service.setCurrentEpisode(null);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class PlayerDurationRecord implements InvalidationListener {
    private final Episode episode;
    private final MediaPlayer player;

    public PlayerDurationRecord(Episode episode, MediaPlayer player) {
        this.episode = episode;
        this.player = player;
    }
    @Override
    public void invalidated(Observable observable) {
        ToDoEpisode todoEpisode = new ToDoEpisode();
        todoEpisode.setPodcast(episode.getPodcast());
        todoEpisode.setEpisode(episode.getTitle());
        todoEpisode.setAudioURL(episode.getAudioURL());
        todoEpisode.setIndex(player.getCurrentTime().toMillis());
        new LocalProfileService().setCurrentEpisode(todoEpisode);
    }
}
