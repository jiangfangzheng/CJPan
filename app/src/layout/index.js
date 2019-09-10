import {Component} from 'react';
import {Layout, Menu, Icon} from 'antd';

const {Header, Footer, Sider, Content} = Layout;
import Link from 'umi/link';
import myStyles from './index.css';

// 引入子菜单组件
const SubMenu = Menu.SubMenu;

export default class BasicLayout extends Component {
    render() {
        return (
            <Layout>
                {/*<Sider width={256} style={{minHeight: '100vh'}}>*/}
                {/*    <div style={{height: '32px', background: 'rgba(255,255,255,.2)', margin: '16px'}}/>*/}
                {/*    <Menu theme="dark" mode="inline" defaultSelectedKeys={['1']}>*/}
                {/*        <Menu.Item key="1">*/}
                {/*            <Link to="/helloworld">*/}
                {/*                <Icon type="pie-chart"/>*/}
                {/*                <span>Helloworld</span>*/}
                {/*            </Link>*/}
                {/*        </Menu.Item>*/}
                {/*        <SubMenu*/}
                {/*            key="sub1"*/}
                {/*            title={<span><Icon type="dashboard"/><span>Dashboard</span></span>}*/}
                {/*        >*/}
                {/*            <Menu.Item key="2"><Link to="/dashboard/analysis">分析页</Link></Menu.Item>*/}
                {/*            <Menu.Item key="3"><Link to="/dashboard/monitor">监控页</Link></Menu.Item>*/}
                {/*            <Menu.Item key="4"><Link to="/dashboard/workplace">工作台</Link></Menu.Item>*/}
                {/*        </SubMenu>*/}
                {/*    </Menu>*/}
                {/*</Sider>*/}
                {/*<Layout>*/}
                {/*    <Header style={{background: '#fff', textAlign: 'center', padding: 0}}>Header</Header>*/}
                {/*    <Content style={{margin: '24px 16px 0'}}>*/}
                {/*        <div style={{padding: 24, background: '#fff', minHeight: 360}}>*/}
                {/*            {this.props.children}*/}
                {/*        </div>*/}
                {/*    </Content>*/}
                {/*    <Footer style={{textAlign: 'center'}}>Ant Design ©2018 Created by Ant UED</Footer>*/}
                {/*</Layout>*/}
                <Layout className="layout">
                    <Header>
                        <div className={myStyles.logo} />
                        <Menu
                            theme="dark"
                            mode="horizontal"
                            defaultSelectedKeys={['1']}
                            style={{ lineHeight: '64px' }}
                        >
                            <Menu.Item key="1"><Link to="/">网盘</Link></Menu.Item>
                            <Menu.Item key="2"><Link to="/settings">设置</Link></Menu.Item>
                            <Menu.Item key="3"><a href="/old">旧版</a></Menu.Item>
                        </Menu>
                    </Header>
                    <Content style={{ padding: '0 50px' }}>
                        {/*<Breadcrumb style={{ margin: '16px 0' }}>*/}
                        {/*    <Breadcrumb.Item>Home</Breadcrumb.Item>*/}
                        {/*    <Breadcrumb.Item>List</Breadcrumb.Item>*/}
                        {/*    <Breadcrumb.Item>App</Breadcrumb.Item>*/}
                        {/*</Breadcrumb>*/}
                        <div style={{ background: '#fff', padding: 24, minHeight: 480, margin: '16px 0' }}>
                            {this.props.children}
                        </div>
                    </Content>
                    <Footer style={{ textAlign: 'center' }}>异想花云 ©2019 Cflower & Sandeepin</Footer>
                </Layout>
            </Layout>
        )
    }
}