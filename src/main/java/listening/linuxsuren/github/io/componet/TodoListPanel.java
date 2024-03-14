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
import listening.linuxsuren.github.io.service.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class TodoListPanel extends JPanel implements ReloadAble {
    private final JList<ToDoEpisode> toDoEpisodeJList = new JList<>();

    public TodoListPanel(TodoEpisodePlayer player) {
        toDoEpisodeJList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getEpisode());
            label.setToolTipText(value.getPodcast());
            return label;
        });
        toDoEpisodeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toDoEpisodeJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }

                ToDoEpisode toDoEpisode = toDoEpisodeJList.getSelectedValue();
                if (toDoEpisode == null) {
                    return;
                }

                Episode toPlayEpisode = toDoEpisode.toEpisode();
                new SimpleCollectionService().getEpisode(new Podcast("", toPlayEpisode.getRssURL())).stream().
                        filter((episode -> episode.equals(toPlayEpisode))).findFirst().ifPresent((episode -> {
                            Platform.runLater(() -> {
                                player.play(toDoEpisodeJList.getSelectedValue());
                            });
                        }));
            }
        });
        addPopupmenu();
        this.add(toDoEpisodeJList);
    }

    private void addPopupmenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem reload = new JMenuItem("Reload");
        reload.addActionListener((e) -> {
            reload();
        });
        menu.add(reload);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    @Override
    public void reload() {
        try {
            Profile profile = new LocalProfileService().getProfile();

            toDoEpisodeJList.setListData(profile.getEpisodes().toArray(new ToDoEpisode[0]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
