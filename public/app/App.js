Ext.application({

    name: "Rovachan",

    appFolder: env.basePath + "assets/app",

    requires: [
        'Ext.ux.IFrame'
    ],

    controllers: [
        'Main',
        'Live',
        'Navigation'
    ],

    models: [
        'Board',
        'Thread'
    ],

    launch: function()
    {
        Ext.create("Rovachan.view.Viewport");
    }
});

/**
 * Simple JSON Post to a relative URL
 *
 * @param {String} url Relative URL
 * @param {SimpleObject} data Data which to send
 * @param {Function} callback callback after success or failure
 */
Ext.ns('Rovachan').postJson = function(url, data, callback)
{
    Ext.Ajax.request({
        url: env.basePath + url,
        method: 'POST',
        jsonData: data,
        success: function() {
            Ext.callback(callback);
        },
        failure: function() {
            Ext.callback(callback);
        }
    });
};