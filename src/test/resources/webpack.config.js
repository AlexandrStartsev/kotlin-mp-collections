const path = require("path");

module.exports = {
    target: 'node',
    entry: "./kotlin-ref-collections-tests.js",
    resolve: {
        modules: [ "node_modules" ],
        alias: {
            "kotlin": path.resolve(__dirname, 'node_modules/kotlin'),
            "kotlin-ref-collections": path.resolve(__dirname, '../classes')
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