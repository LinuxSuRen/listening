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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class MediaBar extends HBox { // MediaBar extends Horizontal Box
    private Slider time = new Slider(); // Slider for time
    private Slider vol = new Slider(); // Slider for volume
    private Button PlayButton = new Button("||"); // For pausing the player
    private Label volume = new Label("Volume: ");
    private MediaPlayer player;

    public MediaBar(MediaPlayer play) {
        player = play;
        time.setShowTickLabels(true);
        vol.setShowTickLabels(true);

        setAlignment(Pos.CENTER); // setting the HBox to center
        setPadding(new Insets(5, 10, 5, 10));
        vol.setPrefWidth(120);
        vol.setMinWidth(30);
        vol.setValue(100);
        HBox.setHgrow(time, Priority.ALWAYS);
        PlayButton.setPrefWidth(30);

        getChildren().add(PlayButton);
        getChildren().add(time);
        getChildren().add(volume);
        getChildren().add(vol);

        // Adding Functionality
        // to play the media player
        PlayButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = player.getStatus();
                if (status == status.PLAYING) {

                    // If the status is Video playing
                    if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {

                        // If the player is at the end of video
                        player.seek(player.getStartTime()); // Restart the video
                        player.play();
                    }
                    else {
                        // Pausing the player
                        player.pause();

                        PlayButton.setText(">");
                    }
                }

                if (status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED) {
                    player.play(); // Start the video
                    PlayButton.setText("||");
                }
            }
        });

        // Providing functionality to time slider
        player.currentTimeProperty().addListener(ov -> updatesValues());

        // Inorder to jump to the certain part of video
        time.valueProperty().addListener(ov -> {
            if (time.isPressed()) { // It would set the time
                // as specified by user by pressing
                player.seek(player.getMedia().getDuration().multiply(time.getValue() / 100));
            }
        });

        // providing functionality to volume slider
        vol.valueProperty().addListener(ov -> {
            if (vol.isPressed()) {
                player.setVolume(vol.getValue() / 100); // It would set the volume
                // as specified by user by pressing
            }
        });
    }

    // Outside the constructor
    protected void updatesValues() {
        Platform.runLater(() -> {
            // Updating to the new time value
            // This will move the slider while running your video
            time.setValue(player.getCurrentTime().toMillis() * 100 / player.getStopTime().toMillis());
        });
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public void reset() {
        time.setValue(0);
        time.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double aDouble) {
                Duration du = player.getStopTime().multiply(aDouble / 100);

                return Math.round(du.toMinutes()) + "m";
            }

            @Override
            public Double fromString(String s) {
                return null;
            }
        });
    }
}