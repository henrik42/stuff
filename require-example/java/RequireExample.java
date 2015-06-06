package foo;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;

class AuxUrlClassLoader extends URLClassLoader {
	public AuxUrlClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	public URL findResource(String name) {
		URL res = super.findResource(name);
		System.out.println("findResource '" + name + "' --> " + res);
		return res;
	}

	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		Enumeration<URL> res = super.findResources(name);
		System.out.println("findResources '" + name + "' --> "
				+ Collections.list(res));
		return res;
	}
}

public class RequireExample {

	static void printResources(ClassLoader pCl, String pResource)
			throws Exception {

		System.out.println("URLs for '" + pResource + "':");
		for (URL url : Collections.list(pCl.getResources(pResource)))
			System.out.println("url = " + url);
	}

	public static void main(String[] args) throws Exception {
		// init RT before Compiler!!!
		IFn require = RT.var("clojure.core", "require");
		IFn second_arg = new AFunction() {
			public Object applyTo(clojure.lang.ISeq arglist) {
				return arglist.next().first();
			};
		};

		/**
		 * <pre>
		 * code1/example/namespace.clj:
		 *  
		 * --- 8< ------ 8< ------ 8< ------ 8< ------ 8< ------ 8< ------ 8< ---
		 * (ns example.namespace (:require [other.namespace :as other]))
		 * (.println System/out "Load code1 example.namespace")
		 * --- 8< ------ 8< ------ 8< ------ 8< ------ 8< ------ 8< ------ 8< ---
		 * 
		 * code2/other/namespace.clj:
		 * 
		 * --- 8< ------ 8< ------ 8< ------ 8< ------ 8< ------ 8< ------ 8< ---
		 * (ns other.namespace)
		 * (.println System/out "Load code2 other.namespace")
		 * --- 8< ------ 8< ------ 8< ------ 8< ------ 8< ------ 8< ------ 8< ---
		 * 
		 * </pre>
		 */
		String code1 = "file:code1/";
		String code2 = "file:code2/";

		ClassLoader urlCl = new AuxUrlClassLoader(new URL[] { new URL(code1),
				new URL(code2) });

		printResources(urlCl, ".");
		printResources(urlCl, "example/namespace.clj");

		clojure.lang.Compiler.LOADER.alterRoot(second_arg,
				RT.seq(new Object[] { urlCl }));
		// Thread.currentThread().setContextClassLoader(cl);

		require.invoke(RT.readString("example.namespace"));
	}
}
