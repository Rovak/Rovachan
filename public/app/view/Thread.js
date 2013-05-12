Ext.define('Rovachan.view.Thread', {

    extend: 'Ext.Panel',

    xtype: 'thread',

    bodyStyle: 'overflow-y: scroll',
    closable: true,
    dockedItems:  [
        {
            dock: 'bottom',
            xtype: 'toolbar',
            items: [
                '->',
                {
                    text: 'Watch thread',
                    action: 'watch-thread'
                }
            ]
        }
    ],

    initComponent: function()
    {
        Ext.applyIf(this, {
            loader: {
                autoLoad: true,
                loadMask: {
                    msg: 'Loading Thread...'
                },
                url: this.url,
                renderer: 'html'
            }
        });

        this.callParent();
    }
});