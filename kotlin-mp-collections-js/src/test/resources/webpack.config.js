const path = require("path");

module.exports = {
    target: 'node',
    entry: "./kotlin-mp-collections-js-tests.js",
    resolve: {
        modules: [ "node_modules" ],
        alias: {
            "kotlin": path.resolve(__dirname, 'node_modules/kotlin'),
            "kotlin-mp-collections-js": path.resolve(__dirname, '../js')
        }
    },
    mode: "development",
	optimization: {
		minimize: false
	}
};