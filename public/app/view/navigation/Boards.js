/**
 * Boards overview
 */
Ext.define('Rovachan.view.navigation.Boards', {

    extend: 'Ext.tree.Panel',

    xtype: 'navigationBoards',

    rootVisible: false,

    initComponent: function()
    {
        this.store = new Ext.data.TreeStore({
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: env.basePath + 'data/boards'
            },
            root: {},
            fields: [
                { name: 'url',  type: 'string' },
                { name: 'text', type: 'string' }
            ],
            folderSort: true,
            sorters: [{
                property: 'text',
                direction: 'ASC'
            }]
        });

        this.callParent();
    }
});