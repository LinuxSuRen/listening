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

package listening.linuxsuren.github.io.service;

import java.io.IOException;

public interface ProfileService {
    Profile getProfile() throws IOException;
    void addQueue(ToDoEpisode episode);
    boolean hasItem(ToDoEpisode episode);
    void removeItem(ToDoEpisode episode);
    void setCurrentEpisode(ToDoEpisode episode);
    void setVolume(int volume);
    void addPersonalRSS(Podcast podcast);
}
