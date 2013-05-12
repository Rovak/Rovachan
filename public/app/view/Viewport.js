Ext.define('Rovachan.view.Viewport', {

    extend: 'Ext.container.Viewport',

    layout: 'border',

    items: [
        {
            id: 'app-header',
            xtype: 'box',
            region: 'north',
            height: 40,
            html: 'Rovachan'
        },
        {
            xtype: 'navigationBoards',
            region: 'west',
            split: true,
            title: 'Boards',
            collapsible: true,
            width: 300
        },
        {
            xtype: 'tabpanel',
            id: 'main-panels',
            region: 'center'
        },
        {
            xtype: 'toolbar',
            region: 'south',
            items: [
                {
                    text: 'Options',
                    menu: {
                        items: [
                            '<b class="menu-title">Choose a Theme</b>',
                            {
                                text: 'Aero Glass',
                                checked: true,
                                group: 'theme'
                            }, {
                                text: 'Vista Black',
                                checked: false,
                                group: 'theme'
                            }, {
                                text: 'Gray Theme',
                                checked: false,
                                group: 'theme'
                            }, {
                                text: 'Default Theme',
                                checked: false,
                                group: 'theme'
                            }
                        ]
                    }
                },
                '->',
                {
                    xtype: 'tbtext',
                    itemId: 'status-text',
                    text: 'Loading...'
                }
            ]
        }
    ]
});