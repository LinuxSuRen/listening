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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BreadCrumbPanel extends JPanel {
    private Container cardContainer;
    private CardLayout cardLayout;
    private Stack<Component> components;
    private Map<String, Component> breadMap = new HashMap<>();

    public BreadCrumbPanel(Container cardContainer, CardLayout cardLayout) {
        this.cardContainer = cardContainer;
        this.cardLayout = cardLayout;
        this.components = new Stack<>();

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public void append(Component com) {
        components.push(com);

        String labelName = "";
        if (this.getComponentCount() > 0) {
            labelName = "> ";
        }
        labelName += com.getName();

        JLabel currentLabel = new JLabel(labelName);
        currentLabel.setName(com.getName());
        this.add(currentLabel);
        breadMap.put(com.getName(), currentLabel);

        // switch the cardLayout content
        cardContainer.add(com, com.getName());
        this.cardLayout.show(cardContainer, com.getName());

        // add click event
        currentLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                components.stream().filter((c) -> {
                    return c.getName().equals(e.getComponent().getName());
                }).findFirst().ifPresent((c -> {
                    int index = -1;
                    for (int i = 0; i < components.size(); i++) {
                        if (components.get(i).getName().equals(c.getName())) {
                            index = i;
                            break;
                        }
                    }

                    if (index == -1) {
                        return;
                    }

                    int total = components.size();
                    for (int i = total - 1; i > index; i--) {
                        Component toDelCom = components.get(i);
                        
                        boolean toDel;
                        if (!(toDelCom instanceof Background)) {
                            toDel = true;
                        } else {
                            Background bg = (Background) toDelCom;
                            toDel = !bg.isRunning();
                        }

                        if (toDel) {
                            components.removeElementAt(i);
                            cardContainer.remove(toDelCom);

                            Component breadCom = breadMap.get(toDelCom.getName());
                            remove(breadCom);
                            breadMap.remove(toDelCom.getName());
                        }
                    }

                    cardLayout.show(cardContainer, c.getName());
                    repaint();
                    revalidate();
                }));
            }
        });
    }

    public void pop(Component com) {
    }
}
