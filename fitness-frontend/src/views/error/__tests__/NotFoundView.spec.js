import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { h } from 'vue'
import NotFoundView from '@/views/error/NotFoundView.vue'

describe('NotFoundView', () => {
  it('应渲染 404 提示信息', () => {
    const wrapper = mount(NotFoundView, {
      global: {
        stubs: {
          'el-button': {
            name: 'ElButton',
            template: '<button class="el-button" @click="$emit(\'click\')"><slot /></button>'
          }
        },
        mocks: {
          $router: { push: () => {} }
        }
      }
    })

    expect(wrapper.text()).toContain('404')
    expect(wrapper.text()).toContain('页面不存在')
  })

  it('应包含返回首页按钮', () => {
    const wrapper = mount(NotFoundView, {
      global: {
        stubs: {
          'el-button': {
            name: 'ElButton',
            template: '<button class="el-button" @click="$emit(\'click\')"><slot /></button>'
          }
        },
        mocks: {
          $router: { push: () => {} }
        }
      }
    })

    const button = wrapper.find('.el-button')
    expect(button.exists()).toBe(true)
    expect(button.text()).toContain('返回首页')
  })
})
