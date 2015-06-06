# stuff

Just code snippets

## require-example

Uses Clojure as a lib from Java. Inserts your own Classloader (an
`URLCLassLoader` in this case) into Clojure so that you can
`(require)` Clojure sources from other places than your file system.

See https://groups.google.com/forum/#!topic/clojure/dY4AmXCDnxw

The code was tested with Clojure 1.5.1

The classloader can be *activated* by altering `Compiler.LOADER` or by
setting the *context classloader*. Don't know what the exact
differences of these two alternatives are.
