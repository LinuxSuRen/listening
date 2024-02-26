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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class EpisodePanel extends JPanel implements Background {
    private final JEditorPane editorPane = new JEditorPane();
    private PlayEvent playEvent;

    public EpisodePanel(Episode episode) {
        this.setName(episode.getTitle());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        editorPane.setContentType("text/html");
        editorPane.setText(episode.getHtmlNote());
        editorPane.setEditable(false);

        this.setLayout(new BorderLayout());
        this.add(createToolPanel(episode), BorderLayout.NORTH);
        this.add(new JScrollPane(editorPane), BorderLayout.CENTER);

    }

    private JPanel createToolPanel(Episode episode) {
        JPanel panel = new JPanel();

        JButton playBut = new JButton("Play");
        JButton showNotesBut = new JButton("ShowNotes");
        JButton rssBut = new JButton("RSS");
        playBut.setToolTipText(episode.getMediaType());

        panel.add(playBut);
        panel.add(showNotesBut);
        panel.add(rssBut);

        playBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    URL audioURL = new URL(episode.getAudioURL());

                    if (playEvent != null) {
                        playEvent.play(audioURL.toURI().toString());
                        playEvent.setTitleLabel(episode.getTitle());
                    }
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
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
                editorPane.setContentType("text/xml");
            }
        });
        return panel;
    }

    public void setPlayEvent(PlayEvent e) {
        playEvent = e;
    }

    @Override
    public boolean isRunning() {
        return playEvent.isPlaying();
    }
}
