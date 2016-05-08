module.exports = function(grunt) {

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
	jshint: {
      files: ['src/main/webapp/js/main.js'],
      options: {
        // options here to override JSHint defaults
        globals: {
          jQuery: true,
          console: true,
          module: true,
          document: true
        }
      }
    },
    uglify: {
      target: {
          options : {
            sourceMap : true,
            sourceMapName : 'src/main/webapp/dist/js/main.map'
          },
          files: {
    	      'src/main/webapp/dist/js/main.min.js': ['src/main/webapp/js/main.js']
          }
      }
    },
    csslint: {
      strict: {
        options: {
          import: 2
        },
        src: ['src/main/webapp/css/styles.css']
      }
    },
    cssmin: {
      target: {
          files: {
    	      'src/main/webapp/dist/css/styles.min.css': ['src/main/webapp/css/styles.css']
          }
      }
    },
	// Files are copied from the node_modules to the src directory to deploy only the relevant files.
	// The node_modules directory contains to many files the project does not depend on. 
	// Bower is no alternative for this problem because it also downloads to many irrelevant files.
	copy: {
	  main: { 
	    files: [
   	      {expand: true, cwd: 'node_modules/', src: ['angular2/**'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['systemjs/dist/system.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['systemjs/dist/system.js.map'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['systemjs/dist/system.src.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['systemjs/dist/system-polyfills.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['es6-promise/dist/es6-promise.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['es6-promise/dist/es6-promise.min.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['es6-shim/es6-shim.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['es6-shim/es6-shim.map'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['es6-shim/es6-shim.min.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['reflect-metadata/Reflect.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['reflect-metadata/Reflect.js.map'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['reflect-metadata/Reflect.ts'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['rxjs/bundles/Rx.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['rxjs/bundles/Rx.min.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['rxjs/bundles/Rx.min.js.map'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['zone.js/dist/zone.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['zone.js/dist/zone.min.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['bootstrap/dist/**'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['jquery/dist/jquery.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['jquery/dist/jquery.min.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['jquery/dist/jquery.min.map'], dest: 'src/main/webapp/lib/', filter: 'isFile'},
	      {expand: true, cwd: 'node_modules/', src: ['typescript/lib/typescript.js'], dest: 'src/main/webapp/lib/', filter: 'isFile'}
	    ]
	  }
	},
	clean: [
		'src/main/webapp/dist',
		'src/main/webapp/lib',
		'node_modules',
		'typings'
	],
	ts: {
	  options: {
		  compiler: "node_modules/typescript/bin/tsc",
		  fast: "never"
	  },
      default: {
		src: ["src/main/webapp/app/**/*.ts"],
		dest: "src/main/webapp/dist/app",
	    tsconfig: 'tsconfig.json'
      }
	},
	watch: {
	  js: {
        files: ['src/main/webapp/js/main.js'],
        tasks: ['jshint', 'uglify']
      },
	  css: {
	    files: ['src/main/webapp/css/styles.css'],
	    tasks: ['cssmin']
      },
	  ts: {
		    files: ['src/main/webapp/app/**/*.ts'],
		    tasks: ['ts']
	      }
	},
  });

  grunt.loadNpmTasks('grunt-contrib-csslint');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks("grunt-ts");
  
  grunt.registerTask('default', ['copy', 'jshint', 'uglify', 'cssmin', 'ts']); //'csslint:strict', 

};