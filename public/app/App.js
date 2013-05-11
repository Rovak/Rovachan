Ext.application({

    name: "Rovachan",

    appFolder: env.basePath + "assets/app",

    requires: [
        'Ext.ux.IFrame'
    ],

    controllers: [
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