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

import listening.linuxsuren.github.io.server.CacheServer;
import listening.linuxsuren.github.io.service.CollectionService;
import listening.linuxsuren.github.io.service.Podcast;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

public class CollectionCardPanel extends JPanel {
    private Podcast podcast;
    private MouseListener mouseListener;

    public CollectionCardPanel(Podcast podcast) {
        this.podcast = podcast;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(new JLabel(podcast.getName()));
    }

    public void asyncLoad(CollectionService collectionService) {
        new Thread(() -> {
            collectionService.loadPodcast(podcast);

            try {
                BufferedImage image = ImageIO.read(CacheServer.wrapURL(podcast.getLogoURL()));

                JLabel label = new JLabel();
                label.setMinimumSize(new Dimension(80, 80));
                label.setIcon(new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
                label.addMouseListener(mouseListener);
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                add(label);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            repaint();
            revalidate();
        }).start();
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        this.mouseListener = l;
        Arrays.stream(this.getComponents()).forEach((c) -> {
            c.addMouseListener(l);
            c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        });
    }
}
