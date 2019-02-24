const path = require("path");

module.exports = {
    target: 'node',
    entry: "./mp-test-tests.js",
    resolve: {
        modules: [ "node_modules" ],
        alias: {
            "kotlin": path.resolve(__dirname, 'node_modules/kotlin'),
            "kotlin-mp-collections-js": path.resolve(__dirname, '../../../kotlin-mp-collections-js/target/js')
        }
    },
    mode: "development",
	optimization: {
		minimize: false
	},
    devtool: "source-map",
    module: {
        rules: [
            {
                test: /\.js$/,
                use: ["source-map-loader"],
                enforce: "pre"
            }
        ]
    }
};