/*
 * Copyright 2024 LinuxSuRen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package listening.linuxsuren.github.io.service;

import java.util.List;

public class PodcastIndex {
    private String indexServer;
    private List<Podcast> items;

    public List<Podcast> getItems() {
        return items;
    }

    public void setItems(List<Podcast> items) {
        this.items = items;
    }

    public String getIndexServer() {
        return indexServer;
    }

    public void setIndexServer(String indexServer) {
        this.indexServer = indexServer;
    }
}
