## Getting started of deploy

### Update application version name
Next, go to the gradle library and raise the version for `appVersionName`.

### Go to Gradle tab
Once you have made changes to the library, you need to open the `gradle` tab,
which is on the right in `Android Studio`. You will open a tab with all your libraries.

### Upload archives
Once the tab is opened, select the saltedge-library in which the changes were made.
Find packages `Tasks` -> `upload` -> and press on `uploadArchives`.

### Change README.md
Update version of modified library

### Commit
After `uploadArchives`, make a commit and push all the changes in github