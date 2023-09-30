# DirectoryWatcher

Simple class to watch a directory and act on file create, modify, delete, and default actions via lambda functions:

```
new DirectoryWatcher(Paths.get("./my/dir/"),
	(filePath) -> {}, // create
	(filePath) -> {}, // modify
	(filePath) -> {}. // delete
	() -> {} // default
);
```
