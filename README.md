# PrivacyPlugin

An IntelliJ plugin built based on [Privado](https://github.com/Privado-Inc/privado)
## How to develop

```bash
git clone https://github.com/irene-hong/privacyPlugin.git
cd privacyPlugin
git checkout -b YOUR_BRANCH_NAME
```
After making changes and commits, do this:
```bash
git branch # you should be in YOUR_BRANCH_NAME
git push origin YOUR_BRANCH_NAME
```
Then go to this git repo page, make a pull request, and notify your team.

## Usage
Need documentation.

## Troubleshooting for developers
- I open this project with IntelliJ, but have trouble importing com.intellij.openai.* libraries
Close IntelliJ, go the project folder and find a folder named `./idea`. 
Delete this folder, and open project with IntelliJ again. Gradle will run initial setup automatically.
After that, you should be able to see all libraries imported properly.


## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)