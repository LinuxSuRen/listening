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
import listening.linuxsuren.github.io.service.LocalProfileService;
import listening.linuxsuren.github.io.service.ToDoEpisode;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class EpisodePanel extends JPanel implements Background {
    private final JEditorPane editorPane = new JEditorPane();
    private final JTextField addressField = new JTextField();
    private PlayEvent playEvent;

    public EpisodePanel(Episode episode) {
        this.setName(episode.getTitle());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        editorPane.setContentType("text/html");
        editorPane.setText(episode.getHtmlNote());
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
                    return;
                }
                playEvent.seek(e.getDescription());
            }
        });
        addressField.setText(episode.getRssURL());

        this.setLayout(new BorderLayout());
        this.add(createToolPanel(episode), BorderLayout.NORTH);
        this.add(new JScrollPane(editorPane), BorderLayout.CENTER);
        this.add(addressField, BorderLayout.SOUTH);
    }

    private JPanel createToolPanel(Episode episode) {
        JPanel panel = new JPanel();

        JButton playBut = new JButton("Play");
        JButton laterBut = new JButton("Later");
        JButton showNotesBut = new JButton("ShowNotes");
        JButton rssBut = new JButton("RSS");
        JButton websiteBut = new JButton("Website");

        // set buttons
        playBut.setToolTipText(episode.getMediaType());
        websiteBut.setVisible(canOpen(episode));

        panel.add(playBut);
        panel.add(laterBut);
        panel.add(showNotesBut);
        panel.add(rssBut);
        panel.add(websiteBut);

        playBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            if (playEvent != null) {
                playEvent.play(episode);
                playEvent.setTitleLabel(episode.getTitle());
            }
            }
        });
        laterBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToDoEpisode todoEpisode = ToDoEpisode.ofEpisode(episode);
                new LocalProfileService().addQueue(todoEpisode);
                laterBut.setEnabled(false);
            }
        });

        showNotesBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorPane.setContentType("text/html");
                editorPane.setText(episode.getHtmlNote());
            }
        });
        rssBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    editorPane.setPage(episode.getRssURL());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                editorPane.setContentType("application/xml;charset=UTF-8");
            }
        });
        websiteBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (websiteBut.isVisible()) {
                    try {
                        Desktop.getDesktop().browse(new URI(episode.getLink()));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        // set status
        ToDoEpisode todoEpisode = ToDoEpisode.ofEpisode(episode);
        laterBut.setEnabled(!new LocalProfileService().hasItem(todoEpisode));
        return panel;
    }

    public void setPlayEvent(PlayEvent e) {
        playEvent = e;
    }

    @Override
    public boolean isRunning() {
        return playEvent.isPlaying();
    }

    private boolean canOpen(Episode episode) {
        return episode.getLink() != null &&
                !episode.getLink().isEmpty() &&
                Desktop.isDesktopSupported() &&
                Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
    }
}
