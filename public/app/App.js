Ext.application({

    name: "Rovachan",

    appFolder: env.basePath + "assets/app",

    requires: [
        'Ext.ux.IFrame'
    ],

    controllers: [
        'Navigation'
    ],

    launch: function()
    {
        Ext.create("Rovachan.view.Viewport");
    }
});