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

import listening.linuxsuren.github.io.service.LocalProfileService;
import listening.linuxsuren.github.io.service.Podcast;
import listening.linuxsuren.github.io.service.SimpleCollectionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddPodcastDialog extends JDialog {
    public AddPodcastDialog(Frame parent) {
        super(parent);
        this.setTitle("Add RSS");
        this.setLocationRelativeTo(parent);
        this.setSize(600, 200);

        JLabel nameLabel = new JLabel();
        JTextField rssField = new JTextField("https://", 40);
        JButton checkBut = new JButton("Check");
        JButton okBut = new JButton("OK");

        checkBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Podcast podcast = new Podcast(nameLabel.getText(), rssField.getText());
                new SimpleCollectionService().loadPodcast(podcast);
                nameLabel.setText(podcast.getName());
                revalidate();
            }
        });
        okBut.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Podcast podcast = new Podcast(nameLabel.getText(), rssField.getText());
                if (podcast.isValid()) {
                    new LocalProfileService().addPersonalRSS(podcast);
                    setVisible(false);
                }
            }
        });

        // compose them together
        JPanel rssPanel = new JPanel();
        rssPanel.add(nameLabel);
        rssPanel.add(rssField);

        JPanel butPanel = new JPanel();
        butPanel.add(checkBut);
        butPanel.add(okBut);

        JPanel rootPanel = new JPanel();
        rootPanel.add(rssPanel);
        rootPanel.add(butPanel);
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));

        this.add(rootPanel);
    }
}
