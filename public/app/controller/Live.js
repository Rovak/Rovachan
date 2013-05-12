/**
 * Handes live updates from the server
 */
Ext.define('Rovachan.controller.Live', {

    extend: 'Ext.app.Controller',

    websocket: null,

    websocketUrl: "ws://localhost:9000/live",

    refs: [
        {
            ref: 'statusText',
            selector: 'viewport #status-text'
        }
    ],


    init: function()
    {
        this.connect(this.websocketUrl);
    },

    /**
     * Connect to the live channel
     */
    connect: function()
    {
        var me = this;

        this.websocket = new WebSocket(this.websocketUrl);
        this.websocket.onopen = function (ev) {

        };
        this.websocket.onclose = function (ev) {

        };
        this.websocket.onmessage = function(ev) {
            var data = JSON.parse(ev.data);

            if (data.action) {
                var action = data.action;
                var method = "on" + action.charAt(0).toUpperCase() + action.slice(1);

                if (typeof me[method] === "function") {
                    me[method](data);
                }
            }
        };
        this.websocket.onerror = function (ev) {

        };
    },

    /**
     * Simpel status updater
     * @param {SimpleObject} data
     */
    onStatus: function(data)
    {
        this.getStatusText().setText(data.message);
    }
});