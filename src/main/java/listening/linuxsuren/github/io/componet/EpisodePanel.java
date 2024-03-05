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

import listening.linuxsuren.github.io.service.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class EpisodePanel extends JPanel implements Background {
    private final JEditorPane editorPane = new JEditorPane();
    private final JTextField addressField = new JTextField();
    private JPanel toolPanel;
    private Episode episode;
    private PlayEvent playEvent;

    public EpisodePanel(Episode episode) {
        this.episode = episode;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPopupMenu editorPopupMenu = createEditorPopupMenu();
        editorPane.setEditable(false);
        BorderUtil.setInsideBorder(editorPane, 8);
        editorPane.addHyperlinkListener((e) -> {
            if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
                return;
            }
            playEvent.seek(e.getDescription());
        });
        editorPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    editorPopupMenu.show(editorPane, e.getX(), e.getY());
                }
            }
        });

        this.toolPanel = createToolPanel();

        this.setLayout(new BorderLayout());
        this.add(toolPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(editorPane), BorderLayout.CENTER);
        this.add(addressField, BorderLayout.SOUTH);

        load();
    }

    private final DataButton<Episode> previousBut = new DataButton<>("Previous");
    private final DataButton<Episode> nextBut = new DataButton<>("Next");
    private final JButton laterBut = new JButton("Later");
    private final JLabel titleLabel = new JLabel();

    private void load() {
        this.setName(episode.getTitle());
        editorPane.setContentType("text/html");
        editorPane.setText(episode.getHtmlNote());
        editorPane.getCaret().setDot(0);
        addressField.setText(episode.getRssURL());
        titleLabel.setText(episode.getPodcast() + " - " + episode.getTitle());

        // set status
        ToDoEpisode todoEpisode = ToDoEpisode.ofEpisode(episode);
        laterBut.setEnabled(!new LocalProfileService().hasItem(todoEpisode));

        // read the next or previous episode
        new Thread(() -> {
            List<Episode> episodes = new SimpleCollectionService().getEpisode(
                    new Podcast("", episode.getRssURL()));
            final int currentIndex = episode.getNumber();
            if (currentIndex < episodes.size()) {
                nextBut.setVisible(true);
                nextBut.setData(episodes.get(currentIndex + 1));
            }
            if (currentIndex > 0) {
                previousBut.setVisible(true);
                previousBut.setData(episodes.get(currentIndex - 1));
            }
        }).start();
    }

    private JPanel createToolPanel() {
        JPanel buttonPanel = new JPanel();

        JButton playBut = new JButton("Play");
        JButton showNotesBut = new JButton("ShowNotes");
        JButton rssBut = new JButton("RSS");
        JButton websiteBut = new JButton("Website");

        // set buttons
        previousBut.setVisible(false);
        nextBut.setVisible(false);
        playBut.setToolTipText(episode.getMediaType());
        websiteBut.setVisible(canOpen(episode));

        buttonPanel.add(previousBut);
        buttonPanel.add(playBut);
        buttonPanel.add(laterBut);
        buttonPanel.add(showNotesBut);
        buttonPanel.add(rssBut);
        buttonPanel.add(websiteBut);
        buttonPanel.add(nextBut);

        previousBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Episode previousEpisode = previousBut.getData();
                if (previousEpisode != null) {
                    episode = previousEpisode;
                    load();
                }
            }
        });
        nextBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Episode nextEpisode = nextBut.getData();
                if (nextEpisode != null) {
                    episode = nextEpisode;
                    load();
                }
            }
        });
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

        JPanel panel = new JPanel();
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.CENTER);
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

    private JPopupMenu createEditorPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem reloadItem = new JMenuItem("Reload");
        reloadItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorPane.getPage() == null) {
                    return;
                }

                try {
                    editorPane.setPage(editorPane.getPage());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        menu.add(reloadItem);
        return menu;
    }
}
