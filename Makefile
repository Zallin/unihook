BOOTSTRAP_SERVER=localhost:9092

.EXPORT_ALL_VARIABLES:

repl:
	clj -m "tools.repl" -p 3000

start:
	clj -m "tools.repl" -p 3000 -f "unihook.core/restart"

build:
	clj -m "tools.build" "target/app.jar"
