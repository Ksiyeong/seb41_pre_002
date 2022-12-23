import React from "react";
import styled from "styled-components";
import UserCardList from "./UserCardList";

const UserDiv = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 24px 16px 0px 30px;

  h1 {
    font-size: 27px;
    margin: 0px 0px 27px;
    color: black;
    font-weight: 400;
  }
  .UserInputDiv {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    width: 1040px;
    height: 50px;
  }
  .ButtonBox {
    display: flex;
    border-color: var(--_bu-outlined-bc-selected);
    border-radius: 10px;
  }
  .Btn {
    /* display: flex; */
    height: 40px;
    width: 80px;
    background-color: var(--_bu-outlined-bg);
    color: #6a737c;
    font-size: 13px;
    font-weight: 400;
    border: 1px solid rgb(214, 217, 220);
    border-radius: 2px;
    &:active {
      border-color: rgb(227, 230, 232);
      background-color: rgb(227, 230, 232);
      color: rgb(59, 64, 60);
    }
    &:hover {
      background-color: rgb(242, 244, 245);
    }
  }
`;

const SubTabDiv = styled.div`
  .SubTab {
    display: flex;
    flex-direction: row;
    margin: 20px 0px 0px 800px ;
  }
  .Tab {
    color: #6a737c;
    font-size: 13px;
    padding: 10px;
    &:active {
      color: black;
      font-weight: 700;
      border-bottom: 2px solid orange;
    }
  }
`;

const Users = () => {
  return (
    <UserDiv>
      <h1>Users</h1>
      <div className="UserInputDiv">
        <div class="flex--item ps-relative mb12">
          <input
            id="tagfilter"
            class="s-input s-input__search h100 js-tag-filter"
            autocomplete="off"
            name="tagfilter"
            type="text"
            maxlength="35"
            placeholder="Filter by user name"
            autofocus=""
          ></input>
          <svg
            aria-hidden="true"
            class="s-input-icon s-input-icon__search svg-icon iconSearch"
            width="18"
            height="18"
            viewBox="0 0 18 18"
          >
            <path d="m18 16.5-5.14-5.18h-.35a7 7 0 1 0-1.19 1.19v.35L16.5 18l1.5-1.5ZM12 7A5 5 0 1 1 2 7a5 5 0 0 1 10 0Z"></path>
          </svg>
        </div>
        <div className="ButtonBox">
          <button className="Btn">Reputation</button>
          <button className="Btn">New users</button>
          <button className="Btn">Voters</button>
          <button className="Btn">Editors</button>
          <button className="Btn">Moderators</button>
        </div>
      </div>
      <SubTabDiv>
        <div className="SubTab">

        <div className="Tab">week</div>
        <div className="Tab">month</div>
        <div className="Tab">quarter</div>
        <div className="Tab">year</div>
        <div className="Tab">all</div>
        </div>
      </SubTabDiv>
      <UserCardList />
    </UserDiv>
  );
};

export default Users;
