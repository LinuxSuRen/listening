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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import listening.linuxsuren.github.io.server.CacheServer;

public class Player extends BorderPane implements PlayEvent {
    private Media media;
    private MediaPlayer player;
    private MediaView view;
    private Pane mpane;
    private MediaBar bar;
    private Label titleLabel;
    public Player(String file) {
        media = new Media(file);
        player = new MediaPlayer(media);
        view = new MediaView(player);
        mpane = new Pane();
        titleLabel = new Label();
        mpane.getChildren().add(view); // Calling the function getChildren

        // inorder to add the view
        setCenter(mpane);
        bar = new MediaBar(player); // Passing the player to MediaBar
        setBottom(bar); // Setting the MediaBar at bottom
        setTop(titleLabel);
        setStyle("-fx-background-color:#bfc2c7");
    }

    public void play() {
        player.stop();
        player.setStartTime(new Duration(0));
        player.play();
    }

    public void play(String audioURL) {
        player.stop();
        player.dispose();

        if (audioURL.startsWith("https://") || audioURL.startsWith("http://")) {
            audioURL = CacheServer.wrap(audioURL);
        }
        media = new Media(audioURL);
        player = new MediaPlayer(media);
        view.setMediaPlayer(player);
        player.play();
        bar.setPlayer(player);
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
}