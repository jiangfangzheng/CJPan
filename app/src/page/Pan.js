import {Breadcrumb, Button, Icon, Input, message, Modal, Progress, Table, Tree, Upload} from 'antd';
import reqwest from 'reqwest';
import DPlayer from "react-dplayer";

export default () => {
    return (<div><Pan/></div>);
}

// 网盘主框架类
class Pan extends React.Component {
    state = {
        dataSource: [],
        selectedRowKeys: [],
        selectedItems: [],
        refreshLoading: false,
        deleteLoading: false,
        newDirLoading: false,
        moveLoading: false,
        renameLoading: false,
        visibleUpdate: false,
        visibleVideo: false,
        visibleMove: false,
        visibleNewDir: false,
        visibleRename: false,
        dirLists: ['Home'],
        movieFileName: "null.null",
        currentPath: "/",
        videoUrl: "/",
        freeSpace: 100,
        totalSpace: 100,
        usageRate: 0,
    };

    newDirName = "";

    newFileName = "";

    // 表格列的项目
    columns = [
        {
            title: '文件',
            dataIndex: 'name',
            render: (text, record) =>
                <div>
                    {record.type == "dir" ? <Icon type="folder-open" theme="filled"/> :
                        <Icon type="file" theme="filled"/>}
                    <a onClick={() => this.onClickName(record)}> {text}</a>
                </div>
            ,
        },
        {
            title: '大小',
            dataIndex: 'size',
            width: 150,
        },
        {
            title: '修改日期',
            dataIndex: 'time',
            width: 200,
        },
    ];

    // 页面初始加载处理
    componentDidMount() {
        // 载入磁盘容量信息
        this.getSpaceSize();
        // 载入根目录文件列表
        this.getFileList("/");
    }

    // 加载指定path下的文件列表
    getFileList = (path) => {
        console.log('getFileList() path:', path);
        let pathArr = this.pathTodirLists(path);
        this.setState({refreshLoading: true});
        reqwest({
            url: '/rest/pan/list',
            method: 'get',
            type: 'json',
            contentType: 'application/json;charset=UTF-8',
            data: {
                path: path,
            },
        }).then(data => {
            console.log("rsp:", data);
            setTimeout(() => {
                this.setState({
                    dataSource: data,
                    currentPath: path,
                    dirLists: pathArr,
                    refreshLoading: false,
                });
            }, 250);
        }).fail((err, msg) => {
            console.log("fail:", msg, err);
            message.error(`载入文件列表失败！`);
            setTimeout(() => {
                this.setState({
                    dataSource: [{
                        "key": "5099331801568296745393",
                        "name": "测试",
                        "link": "/测试",
                        "size": "Directory",
                        "time": "2019-09-10 23:23:00",
                        "type": "dir",
                        "description": null,
                        "transcode": "noneed"
                    }],
                    currentPath: path,
                    dirLists: pathArr,
                    refreshLoading: false,
                });
            }, 250);
        }).always((resp) => {
            console.log("always:", resp);
        });
    };

    // 获取磁盘容量信息
    getSpaceSize = () => {
        console.log('getSpaceSize()');
        reqwest({
            url: '/rest/pan/space',
            method: 'get',
            type: 'json',
            contentType: 'application/json;charset=UTF-8',
        }).then(data => {
            console.log("rsp:", data);
            if (data.success == true) {
                console.log("data.msg:", data.msg);
                let msgJson = JSON.parse(data.msg);
                console.log("data.msg.freeSpace:", msgJson.freeSpace);
                console.log("data.msg.totalSpace:", msgJson.totalSpace);
                let usedSpace = msgJson.totalSpace - msgJson.freeSpace;
                this.setState({
                    freeSpace: msgJson.freeSpace,
                    totalSpace: msgJson.totalSpace,
                    usageRate: (usedSpace / msgJson.totalSpace * 100).toFixed(2),
                });
            }
        }).fail((err, msg) => {
            console.log("fail:", msg, err);
        }).always((resp) => {
            console.log("always:", resp);
        });
    };

    // 面包屑path数组处理 (/1/2/3 处理为 1 1/2 1/2/3)
    pathTodirLists = (path) => {
        let pathArr = path.split('/');
        pathArr[0] = 'Home';
        // 数组中每一个是当前的相对path
        for (let i = 1; i < pathArr.length; i++) {
            pathArr[i] = pathArr[i - 1] + '/' + pathArr[i];
        }
        return pathArr;
    };

    // 点击删除按钮处理事件
    onClickDelete = () => {
        this.setState({deleteLoading: true});
        console.log("删除的文件：", this.state.selectedItems);
        let selectedData = this.state.selectedItems;
        // 构造请求json体
        let deleteJson = [];
        for (let i = 0; i < selectedData.length; i++) {
            deleteJson.push({
                name: selectedData[i].name,
                path: this.state.currentPath + "/"
            })
        }
        console.log("删除的文件josn：", deleteJson);
        reqwest({
            url: '/rest/pan/delete',
            method: 'delete',
            type: 'json',
            contentType: 'application/json;charset=UTF-8',
            data: JSON.stringify(deleteJson),
        }).then(data => {
            console.log("rsp:", data);
            if (data.success == true) {
                setTimeout(() => {
                    this.setState({
                        deleteLoading: false,
                    });
                }, 250);
                this.getFileList(this.state.currentPath);
            } else {
                message.error(`删除文件失败！`);
            }
        }).fail((err, msg) => {
            console.log("fail:", msg, err);
            message.error(`删除文件失败！`);
        }).always((resp) => {
            console.log("always:", resp);
            this.setState({
                selectedItems: [],
                selectedRowKeys: [],
            });
        });
    };

    // 点击分享按钮处理事件
    onClickShare = () => {
        console.log("onClickShare()");
    };

    // 表格勾选项变化事件
    onSelectChange = selectedRowKeys => {
        console.log('selectedRowKeys changed: ', selectedRowKeys);
        let result = this.getSelectItemByKey(selectedRowKeys);
        console.log(result);
        this.setState({
            selectedRowKeys,
            selectedItems: result,
        });
    };

    getSelectItemByKey = (key) => {
        let items = this.state.dataSource;
        let result = [];
        for (let i = 0; i < items.length; i++) {
            for (let j = 0; j < key.length; j++) {
                if (items[i].key == key[j]) {
                    result.push(items[i]);
                }
            }
        }
        return result;
    };

    // 显示上传文件弹出框
    showModalUpdate = () => {
        this.setState({
            visibleUpdate: true,
        });
    };

    // 表格中文件名点击事件
    onClickName = (record) => {
        // 当前域名url
        let url = window.location.href;
        console.log("text:", record, "url:", url);
        if ("dir" == record.type) {
            console.log("dir");
            // 文件夹：打开
            this.getFileList(record.link);
        } else if ("mp4" == record.type) {
            console.log("mp4", record.link);
            // mp4格式：预览
            // 编码以解决特殊字符下载不了文件问题
            let encodeLink = encodeURI(record.link);
            this.setState({
                visibleVideo: true,
                videoUrl: url + encodeLink,
                movieFileName: record.name,
            });
        } else {
            // 普通文件：下载
            console.log("file: ", record.link);
            let a = document.createElement('a');
            a.href = record.link;
            a.download = record.name;
            a.click();
        }
    };

    // 移动文件按钮点击事件
    moveOnClick = () => {
        this.setState({
            visibleMove: true,
        });
    };

    // 弹出框确认按钮事件（刷新当前path下文件列表）
    handleOk = e => {
        this.setState({
            visibleUpdate: false,
        });
        // 刷新文件列表
        this.getFileList(this.state.currentPath);
    };

    // 弹出框取消按钮事件（刷新当前path下文件列表）
    handleCancel = e => {
        this.setState({
            visibleUpdate: false,
        });
        // 刷新文件列表
        this.getFileList(this.state.currentPath);
    };

    // 视频预览弹出框取消按钮事件
    handleCancelVideo = e => {
        this.setState({
            visibleVideo: false,
        });
    };

    // 移动预览弹出框取消按钮事件
    handleCancelMove = e => {
        console.log(e);
        this.setState({
            visibleMove: false,
        });
    };

    // 刷新按钮点击事件
    onClickRefresh = () => {
        this.getFileList(this.state.currentPath);
        this.setState({
            selectedItems: [],
            selectedRowKeys: [],
        });
    };

    // 新建文件夹按钮点击事件
    onClickNewDir = () => {
        this.setState({
            visibleNewDir: true,
        });
    };

    // 面包屑点击后刷新对应path的文件列表
    breadcrumbOnClick = (e) => {
        console.log("breadcrumbOnClick:", e);
        // 替换Home为'' (Home/1/2/3 处理为 /1/2/3)
        if (e == 'Home') {
            e = 'Home/'
        }
        let arr = e.split('/');
        arr[0] = '';
        let path = arr.join("/");
        console.log("breadcrumbOnClick path:", path);
        this.getFileList(path);
    };

    // 完整路径提取最后的路径名(1/2/3 处理为 3)
    fullPathToLastName = (e) => {
        let arr = e.split('/');
        let lastName = arr[arr.length - 1];
        console.log("lastName: ", lastName);
        return lastName;
    };

    // 新建文件夹弹出框 确认 按钮事件
    handleNewDirOk = () => {
        if (this.newDirName == "") {
            message.error(`文件夹名为空！`);
            return;
        }
        this.setState({
            newDirLoading: true,
        });
        console.log("handleNewDirOk()");
        let json = {
            name: this.newDirName,
            path: this.state.currentPath,
        };
        reqwest({
            url: '/rest/pan/newdir',
            method: 'post',
            type: 'json',
            contentType: 'application/json;charset=UTF-8',
            data: JSON.stringify(json),
        }).then(data => {
            console.log("rsp:", data);
            this.newDirName = "";
            setTimeout(() => {
                this.setState({
                    visibleNewDir: false,
                    newDirLoading: false,
                });
            }, 250);
        }).fail((err, msg) => {
            console.log("fail:", msg, err);
            message.error(`新建文件夹失败！`);
            setTimeout(() => {
                this.setState({
                    newDirLoading: false,
                });
            }, 250);
        }).always((resp) => {
            console.log("always:", resp);
        });
    };

    // 新建文件夹弹出框 取消 按钮事件
    handleNewDirCancel = () => {
        this.newDirName = "";
        this.setState({
            visibleNewDir: false,
        });
    };

    // 新建文件夹名字输入框变化事件
    onChangeNewDir = e => {
        console.log(e.target.value);
        this.newDirName = e.target.value;
    };

    // 重命名按钮点击事件
    onClickRename = () => {
        this.setState({
            visibleRename: true,
        });
    };

    // 重命名名字输入框变化事件
    onChangeRemane = e => {
        console.log(e.target.value);
        this.newFileName = e.target.value;
    };

    // 重命名弹出框 确认 按钮事件
    handleRenameOk = () => {
        let oldName = this.state.currentPath + "/" + this.state.selectedItems[0].name;
        let newName = this.state.currentPath + "/" + this.newFileName;
        console.log("原名字before：", oldName);
        console.log("新名字after：", newName);
        if (this.newFileName == "") {
            message.error(`文件名为空！`);
            return;
        }
        this.setState({
            renameLoading: true,
        });
        console.log("handleRenameOk()");
        let json = {
            before: oldName,
            after: newName,
        };
        reqwest({
            url: '/rest/pan/rename',
            method: 'put',
            type: 'json',
            contentType: 'application/json;charset=UTF-8',
            data: JSON.stringify(json),
        }).then(data => {
            console.log("rsp:", data);
            this.newFileName = "";
            setTimeout(() => {
                this.setState({
                    visibleRename: false,
                    renameLoading: false,
                    selectedItems: [],
                    selectedRowKeys: [],
                });
            }, 250);
            this.getFileList(this.state.currentPath);
        }).fail((err, msg) => {
            console.log("fail:", msg, err);
            message.error(`重命名文件失败！`);
            setTimeout(() => {
                this.setState({
                    renameLoading: false,
                });
            }, 250);
        }).always((resp) => {
            console.log("always:", resp);
        });
    };

    // 重命名弹出框 取消 按钮事件
    handleRenameCancel = () => {
        this.newFileName = "";
        this.setState({
            visibleRename: false,
            selectedItems: [],
            selectedRowKeys: [],
        });
    };

    render() {
        const {selectedRowKeys} = this.state;
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
        };
        const hasSelected = selectedRowKeys.length > 0;
        const hasSelectedOne = selectedRowKeys.length == 1;

        // 上传文件
        const props = {
            name: 'file',
            action: '/rest/pan/upload',
            data: {
                path: this.state.currentPath,
            },
            onChange(info) {
                if (info.file.status !== 'uploading') {
                    console.log(info.file, info.fileList);
                }
                if (info.file.status === 'done') {
                    message.success(`${info.file.name} 上传成功！`);
                } else if (info.file.status === 'error') {
                    message.error(`${info.file.name} 上传失败！`);
                }
            },
        };

        return (
            <div>
                <div style={{marginBottom: 16}}>
                    {/*按钮*/}
                    <Button type="primary" style={{marginRight: 8}} onClick={this.showModalUpdate}>上传</Button>
                    <Button style={{marginRight: 8}} onClick={this.onClickRefresh}>刷新</Button>
                    <Button style={{marginRight: 8}} onClick={this.onClickNewDir}>新建文件夹</Button>
                    <Button style={{marginRight: 8}} onClick={this.onClickRename}
                            disabled={!hasSelectedOne}>重命名</Button>
                    <Button style={{marginRight: 8}} onClick={this.onClickShare} disabled={!hasSelectedOne}>分享</Button>
                    <Button type="danger" style={{marginRight: 8}} onClick={this.onClickDelete} disabled={!hasSelected}
                            loading={this.state.deleteLoading}>删除</Button>
                    <Button onClick={this.moveOnClick} disabled={!hasSelected}
                            loading={this.state.moveLoading}>移动</Button>
                    <div style={{float: "right", width: "180px"}}>
                        总共:{this.state.totalSpace}G 剩余:{this.state.freeSpace}G
                        <Progress percent={this.state.usageRate} status="active"/>
                    </div>

                    {/*上传界面弹出框*/}
                    <Modal
                        title="上传"
                        visible={this.state.visibleUpdate}
                        onOk={this.handleOk}
                        onCancel={this.handleCancel}
                        footer={null}
                    >
                        <p>上传路径：{this.state.currentPath}</p>
                        <Upload {...props}>
                            <Button>
                                <Icon type="upload"/> 选择文件
                            </Button>
                        </Upload>
                    </Modal>

                    {/*视频预览界面弹出框*/}
                    <Modal
                        title={this.state.movieFileName}
                        visible={this.state.visibleVideo}
                        onOk={this.handleOk}
                        onCancel={this.handleCancelVideo}
                        footer={null}
                        destroyOnClose={true}
                        width='75%'
                    >
                        <DPlayer
                            options={{
                                video: {url: this.state.videoUrl},
                                autoplay: true,
                            }}
                        />
                    </Modal>

                    {/*新建文件夹弹出框*/}
                    <Modal
                        title="新建文件夹"
                        visible={this.state.visibleNewDir}
                        onOk={this.handleNewDirOk}
                        confirmLoading={this.state.newDirLoading}
                        onCancel={this.handleNewDirCancel}
                        destroyOnClose={true}
                    >
                        <p>新建文件夹路径：{this.state.currentPath}</p>
                        <Input placeholder="请输入文件夹名…" allowClear onChange={this.onChangeNewDir}/>
                    </Modal>

                    {/*重命名弹出框*/}
                    <Modal
                        title="重命名"
                        visible={this.state.visibleRename}
                        onOk={this.handleRenameOk}
                        confirmLoading={this.state.renameLoading}
                        onCancel={this.handleRenameCancel}
                        destroyOnClose={true}
                    >
                        <p>原名字：{this.state.selectedItems.length > 0 ? this.state.selectedItems[0].name : ""}</p>
                        <Input placeholder="请输入新名字…" allowClear onChange={this.onChangeRemane}/>
                    </Modal>

                    {/*移动界面弹出框*/}
                    <Modal
                        title={this.state.movieFileName}
                        visible={this.state.visibleMove}
                        onOk={this.handleOk}
                        onCancel={this.handleCancelMove}
                        footer={null}
                        destroyOnClose={true}
                        width='50%'
                    >
                        <Button type="primary">确认</Button> 移动到：。。。
                        <MoveTree/>
                    </Modal>
                    <span style={{marginLeft: 8}}>{hasSelected ? `已选择 ${selectedRowKeys.length} 项` : ''}</span>
                </div>
                {/*目录导航面包屑*/}
                <Breadcrumb>
                    {this.state.dirLists.map((number) =>
                        <Breadcrumb.Item><a
                            onClick={() => this.breadcrumbOnClick(number)}>{this.fullPathToLastName(number)}</a></Breadcrumb.Item>
                    )}
                </Breadcrumb>
                {/*数据表格*/}
                <Table rowSelection={rowSelection} columns={this.columns} dataSource={this.state.dataSource}
                       loading={this.state.refreshLoading}/>
            </div>
        );
    }
}

// 移动操作的展示树
const {TreeNode} = Tree;

class MoveTree extends React.Component {
    state = {
        treeData: [
            {title: 'Expand to load', key: '0'},
            {title: 'Expand to load', key: '1'},
            {title: 'Tree Node', key: '2', isLeaf: true},
        ],
    };

    onLoadData = treeNode =>
        new Promise(resolve => {
            if (treeNode.props.children) {
                resolve();
                return;
            }
            setTimeout(() => {
                this.setState({
                    treeData: [...this.state.treeData],
                });
                resolve();
            }, 500);
        });

    renderTreeNodes = data =>
        data.map(item => {
            if (item.children) {
                return (
                    <TreeNode title={item.title} key={item.key} dataRef={item}>
                        {this.renderTreeNodes(item.children)}
                    </TreeNode>
                );
            }
            return <TreeNode key={item.key} {...item} dataRef={item}/>;
        });

    render() {
        return <Tree loadData={this.onLoadData}>{this.renderTreeNodes(this.state.treeData)}</Tree>;
    }
}

